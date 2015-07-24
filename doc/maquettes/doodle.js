//
// 03/04/2015
//

	function Doodle(url) {
			
		this.url = url;
		this.doodleJS = {
			data : null/*JSON.parse(...)*/
		}
		
		this.setDoodleJS = function(doodleJS_string) {
			this.doodleJS = {
				data : JSON.parse(doodleJS_string)
			}
		}
	
		/**
		 * @param i numéro du jour, 0 étant le prochain match (ou le match d'aujourd'hui)
		 * @param fcOptions par défaut : doodleJS.data.poll.fcOptions
		 */
		this.getDate = function(i, fcOptions) {
			if (typeof  fcOptions == 'undefined') fcOptions = this.doodleJS.data.poll.fcOptions
			return new Date(fcOptions[i].start * 1000);
		};
	
		this.getDates = function(fcOptions) {
			var dates = [];
			if (typeof fcOptions == 'undefined') fcOptions = this.doodleJS.data.poll.fcOptions
			$.each(this.doodleJS.data.poll.fcOptions, function(k, v) {
				dates.push(this.getDate(k, fcOptions));
			});
			return dates;
		};
		
		/**
		 * On ajoute à chaque participant un champ "update" pour indiquer la date où on a regardé le Doodle
		 * @param i numéro du jour, par défaut : 0
		 */
		this.getParticipants = function(i) {
			var now = new Date();
			if (typeof i == 'undefined') i = 0;
			var participants = [];
			$.each(this.doodleJS.data.poll.participants, function(k, participant) {
				participant.update = Math.round(now.getTime() / 1000); // en second pour Doodle
				if (participant.preferences[i] == 'y') {
					participants.push(participant);
				}
			});
			return participants;
		};
		
	}