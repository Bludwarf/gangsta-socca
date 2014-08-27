- le navigateur doit autoriser les Cookies pour accéder au trombi

VERSIONS :

	2.4 :
		- Génération d'un Meeting Outlook

	2.3 :
		- Repo des matches
		- Accents mal écrit mais bien récupéré du Doodle (exemple "Xavì")
			- S'affiche bien en JUnit mais voir si Console en UTF-8 ? => /GangstaSocca/src/test/java/fr/bludwarf/gangstasocca/DoodleConnectorTest.java
		- Afficher le nombre de sandwiches comme le nombre de joueurs

	2.2 :
		- Génération HTML du prochain match
		
	2.1 :
		- Ordre des joueurs = ordre du Doodle
		- UTF-8
		- Template + exe copié dans la Dropbox lors du deploy
		- Ouverture auto du fichier généré + ouverture du mail
		
	2.0 :
		- Page HTML à la place de prochainMatch.txt :
			- Créer une page HTML avec un mailto: (http://fr.wikipedia.org/wiki/Mailto)
		- Valeurs par défaut pour : email joeurs et nom complet joueurs + sandwiche
		- Le pseudo des joueurs affiché est celui du Doodle
		- Intégrer les informations du fichier mailMickaël.txt

	0.2 :
		- Ne pas réinterroger le trombi si erreur 500 si une personne