	// Spécifique Soccer
	
	/**
	 * Le vrai nom d'un sandwich
	 */
	Doodle.prototype.getSandwiche = function(nom) {
		return nom.toLocaleLowerCase();
	}
	
	/**
	 * La liste des sandwiches avec leur nombre.
	 * Stocke en effet de bord chaque sandwich dans le JSON ainsi que son pseudo.
	 * @param participants par défaut les participants du prochain match
	 */
	Doodle.prototype.getSandwiches = function(participants) {
		if (typeof participants == 'undefined') participants = this.getParticipants(0);
		var doodle = this;
	
		var regex = /^(.+) *\((.+)\)$/;
		var sandwiches = {};
		$.each(participants, function(k, participant) {
			if (typeof participant.sandwich == 'undefined') {
				if (participant.name.indexOf('(') > -1) {
					var groups = regex.exec(participant.name);
					participant.pseudo   = groups[1];
					participant.sandwich = groups[2];
				}
				else {
					participant.pseudo = participant.name;
					participant.sandwich = null; // pas de sandwich
				}
			}
			
			if (participant.sandwich != null) {
				var s = participant.sandwich;
				if (sandwiches[s] == null) {
					sandwiches[s] = 0;
				}
				++sandwiches[s];
			}
		});
		
		// Il faut regrouper les noms qui correspondent au même sandwich
		var sandwichesClean = {};
		$.each(sandwiches, function(nom, nb) {
			var vraiNom = doodle.getSandwiche(nom);
			if (sandwichesClean[vraiNom] == null) sandwichesClean[vraiNom] = 0;
			sandwichesClean[vraiNom] += nb;
		});
		
		return sandwichesClean;
	}