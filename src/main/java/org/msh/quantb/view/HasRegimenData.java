package org.msh.quantb.view;

import java.util.List;

import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;

/**
 * has list of {@link ForecastingRegimenUIAdapter}
 * @author Alex Kurasoff
 *
 */
public interface HasRegimenData {
	
	List<ForecastingRegimenUIAdapter> getData();

}
