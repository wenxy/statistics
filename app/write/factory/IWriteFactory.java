package write.factory;

import constants.Action;
import write.IWrite;

public interface IWriteFactory {
	 
	public IWrite getWriteInstance(String caller,Action action);
	
}
