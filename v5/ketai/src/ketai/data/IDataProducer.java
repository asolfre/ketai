package ketai.data;


public interface IDataProducer {

	void registerDataConsumer(IDataConsumer _dataConsumer);
	
	void removeDataConsumer(IDataConsumer _dataConsumer);
}
