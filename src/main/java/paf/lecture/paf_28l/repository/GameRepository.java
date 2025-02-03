package paf.lecture.paf_28l.repository;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class GameRepository {
    @Autowired
    private MongoTemplate template;

    /*
     *  db.games.aggregate([
     *      {$match: {name: {regex: <name>, $options: 'i'}}},
     *      {$project: {name:1, ranking:1, image:1, _id:-1}},
     *      {$sort: {ranking: 1}},
     *      {$limit: 3}
     *  ])
     */
    public List<Document> findGamesByName(String name) {
        // Create aggregation stages
        Criteria c = Criteria.where("name").regex(name, "i");
        MatchOperation matchName = Aggregation.match(c);

        // Projection attributes
        ProjectionOperation projectFields = Aggregation.project("name", "ranking", "image")
            .andExclude("_id");

        // Sort by ranking
        SortOperation sortByRanking = Aggregation.sort(Sort.Direction.ASC, "ranking");

        // Take the top 3 results
        LimitOperation limit3 = Aggregation.limit(3);

        // Create the pipeline
        Aggregation pipeline = Aggregation.newAggregation(matchName, projectFields, sortByRanking, limit3);

        // Run the aggregation
        AggregationResults<Document> result = template.aggregate(pipeline, "games", Document.class);
        return result.getMappedResults();
    }

    /*
     *  db.games.aggregate([
     *      {$group: {
     *          _id: '$user',
     *          comments: {
     *              $push: {
     *                  gid: '$gid',
     *                  text: '$c_text'
     *              }
     *          }
     *      }}
     *  ])
     */
    public List<Document> groupCommentsByUser() {
        GroupOperation groupByUser = Aggregation.group("user")
            .push("c_text").as("comments")
            .push("gid").as("gid");

        LimitOperation limit = Aggregation.limit(3);

        Aggregation pipeline = Aggregation.newAggregation(groupByUser, limit);

        AggregationResults<Document> result = template.aggregate(pipeline, "comments", Document.class);
        return result.getMappedResults();
    }

    public List<Document> groupCommentsByUser2() {
        GroupOperation groupByUser = Aggregation.group("user")
            .push(new BasicDBObject()
                .append("gid", "$gid")
                .append("text", "$c_text"))
            .as("comments");

        LimitOperation limit = Aggregation.limit(3);

        Aggregation pipeline = Aggregation.newAggregation(groupByUser, limit);

        AggregationResults<Document> result = template.aggregate(pipeline, "comments", Document.class);
        return result.getMappedResults();
    }

    public Document joinGameCommentsByUser(String user, Optional<Integer> limit) {
        
        SortOperation sortRating = Aggregation.sort(Sort.Direction.DESC, "rating");
        LookupOperation lookup;
        if (limit.isEmpty()) {
            lookup = LookupOperation.newLookup()
            .from("comments")
            .localField("_id")
            .foreignField("gid")
            .pipeline(sortRating)
            .as("reviews");
        } else {
            LimitOperation limitOperation = Aggregation.limit(limit.get());
            lookup = LookupOperation.newLookup()
            .from("comments")
            .localField("_id")
            .foreignField("gid")
            .pipeline(sortRating, limitOperation)
            .as("reviews");
        }
        UnwindOperation unwind = Aggregation.unwind("reviews");
        
        GroupOperation groupByUser = Aggregation.group("$reviews.user")
                .push(new BasicDBObject()
                        .append("name", "name")
                        .append("rating", "$reviews.rating")
                        .append("comment", "$reviews.c_text"))
                .as("reviews");
        
        Criteria userCriteria = Criteria.where("_id").is(user); //rmbr that user is id now
        MatchOperation userMatch = Aggregation.match(userCriteria);

        LimitOperation limitOne = Aggregation.limit(1);

        Aggregation pipeline = Aggregation.newAggregation(lookup, unwind, groupByUser, userMatch, limitOne);
        AggregationResults<Document> result = template.aggregate(pipeline, "games", Document.class);

        return result.getMappedResults().getFirst();
    }
}
