package application.dao.implementations.mongodb;


import application.models.Wave;
import application.models.id.Id;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaveDAOMongoDB extends MongoRepository<Wave, Id<Wave>> {
}
