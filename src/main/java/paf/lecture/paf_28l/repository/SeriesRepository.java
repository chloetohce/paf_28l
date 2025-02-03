package paf.lecture.paf_28l.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.stereotype.Repository;

@Repository
public class SeriesRepository {
    @Autowired
    private MongoTemplate template;

    /*
     *  db.series.aggregate([
     *      {$unwind: '$genres'},
     *      {$group: {
     *          _id: '$genres',
     *          count: {$sum: 1}
     *      }},
     *      {$sort: {_id: -1}}
     *  ])
     */
    public List<Document> listSeriesByGenres() {
        UnwindOperation unwind = Aggregation.unwind("genres");
        GroupOperation groupAndCountGenres = Aggregation.group("genres") //here genres becomes id, so we need to sort by id
            .count().as("count");

        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "_id");

        Aggregation pipeline = Aggregation.newAggregation(unwind, groupAndCountGenres, sort);

        return template.aggregate(pipeline, "shows", Document.class).getMappedResults();
    }
    
}
