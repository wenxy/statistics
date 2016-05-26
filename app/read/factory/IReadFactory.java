package read.factory;

import constants.KPI;
import read.IRead;

public interface IReadFactory {
	public IRead getReadInstance(String caller,KPI kpi);
}
