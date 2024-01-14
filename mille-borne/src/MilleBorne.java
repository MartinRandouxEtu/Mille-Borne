import extensions.CSVFile;

class MilleBorne extends Program{
    final char NOUVELLE_LIGNE = '\n';

    final String CHEMIN_AFFICHAGE = "ressources/affichage/";
    final String CHEMIN_CARTE = "ressources/cartes/";
    final String CHEMIN_QUESTIONS = "ressources/questions/";
    final String CHEMIN_SAUVEGARDE = "ressources/sauvegarde/";

    final String HAUT_ROUTE = chargerRessource("partie_haute_route.txt");
    final String BAS_ROUTE = chargerRessource("partie_basse_route.txt");
    final String REGLE_DU_JEU = chargerRessource("regledujeu.txt");
    final String FOND_GAGNANT = chargerRessource("gagnant/fond.txt");

    final String MENU = chargerRessource("menu.txt");
    final String MENU_NB_JOUEUR = chargerRessourceSansRetourDerniereLigne("nbjoueur.txt");

    final String[] CARTE_25_BORNES = chargerRessourceTableau(CHEMIN_CARTE + "25_bornes.txt");
    final String[] CARTE_50_BORNES = chargerRessourceTableau(CHEMIN_CARTE + "50_bornes.txt");
    final String[] CARTE_75_BORNES = chargerRessourceTableau(CHEMIN_CARTE + "75_bornes.txt");
    final String[] CARTE_100_BORNES = chargerRessourceTableau(CHEMIN_CARTE + "100_bornes.txt");
    final String[] CARTE_200_BORNES = chargerRessourceTableau(CHEMIN_CARTE + "200_bornes.txt");
    final String[] CARTE_FIN_LIMITATION = chargerRessourceTableau(CHEMIN_CARTE + "fin_limitation.txt");
    final String[] CARTE_LIMITATION = chargerRessourceTableau(CHEMIN_CARTE + "limitation.txt");

    final String[][] QUESTIONS_NORMAL = recupererQuestion("questions_normal");
    final String[][] QUESTIONS_DIFFICILE = recupererQuestion("questions_difficile");

    // fonction qui sert à executer les fonctions de sauvegarde
    void sauvegarder(int numTour, Joueur[] listeJoueurs, Pioche pioche, int joueurActif){
        sauvegarderTour(numTour, joueurActif, length(listeJoueurs));
        sauvegarderJoueurs(listeJoueurs);
        sauvegarderPioche(pioche);
    }

    // fonction qui sauvegarde le indice du joueur actif et le numero
    // du tour dans le fichier tour.csv
    void sauvegarderTour(int numTour, int joueurActif, int nbJoueur){
        joueurActif = changerJoueur(nbJoueur, joueurActif);
        if(joueurActif == 0){
            numTour++;
        }
        String[][] contenue = new String[][]{{""+numTour}, {""+joueurActif}};
        saveCSV(contenue, CHEMIN_SAUVEGARDE + "tour.csv");
    }

    // fonction qui sauvegarde toutes les informations de tous les joueurs
    // dans le fichier joueurs.csv
    void sauvegarderJoueurs(Joueur[] listeJoueurs){
        String[][] contenue = new String[length(listeJoueurs)*17+1][1];
        contenue[0][0] = "" + length(listeJoueurs);
        for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
            final int IDC_JOUEUR = numJoueur * 17;
            Joueur j = listeJoueurs[numJoueur];
            contenue[IDC_JOUEUR + 1][0] = "" + j.numeroJoueur;
            contenue[IDC_JOUEUR + 2][0] = "" + j.avancer;
            contenue[IDC_JOUEUR + 3][0] = "" + j.limitationVitesse;
            contenue[IDC_JOUEUR + 4][0] = "" + j.nbQuestionLimitation;
            contenue[IDC_JOUEUR + 5][0] = "|" + j.espaceVoiture + "|";
            for(int numCarte = 0; numCarte < 6; numCarte++){
                final int IDC_JOUEUR_CARTE = IDC_JOUEUR + numCarte * 2;
                contenue[IDC_JOUEUR_CARTE + 6][0] = "" + j.mainJoueur[numCarte].type;
                contenue[IDC_JOUEUR_CARTE + 7][0] = "" + j.mainJoueur[numCarte].borne;
            }
        }
        saveCSV(contenue, CHEMIN_SAUVEGARDE + "joueurs.csv");
    }

    // fonction qui sauvegarde toutes les cartes de le pioche et où on est 
    // arrivé dans la pioche dans me fichier pioche.csv
    void sauvegarderPioche(Pioche pioche){
        String[][] contenue = new String[113][1];
        contenue[0][0] = "" + pioche.indiceDerniereCarte;
        for(int numCarte = 0; numCarte < 56; numCarte++){
            final int IDC_CARTE = numCarte * 2;
            contenue[IDC_CARTE + 1][0] = "" + pioche.pioche[numCarte].type;
            contenue[IDC_CARTE + 2][0] = "" + pioche.pioche[numCarte].borne;
        }
        saveCSV(contenue, CHEMIN_SAUVEGARDE + "pioche.csv");
    }

    // fonction qui recupére la dernière sauvegarde du fichier tour.csv 
    int[] chargerTour(){
        int[] tabTour = new int[2];
        CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDE + "tour.csv");
        tabTour[0] = stringToInt(getCell(fichier, 0, 0));
        tabTour[1] = stringToInt(getCell(fichier, 1, 0));
        return tabTour;
    }

    // fonction qui à l'aide d'autre fonction récupére toutes les données
    // de tous les joueurs
    Joueur[] chargerJoueurs(){
        CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDE + "joueurs.csv");
        int nombreJoueurs = stringToInt(getCell(fichier, 0, 0));
        Joueur[] listeJoueurs = new Joueur[nombreJoueurs];
        for(int numJoueur = 0; numJoueur < nombreJoueurs; numJoueur++){
            listeJoueurs[numJoueur] = recupererDonneeJoueur(fichier, numJoueur);
            recupererCartesJoueur(fichier, listeJoueurs[numJoueur], numJoueur);
        }
        return listeJoueurs;
    }

    // fonction pour récuperer les information dur un joueur pour le recréer
    Joueur recupererDonneeJoueur(CSVFile fichier, int numJoueur){
        final int IDC_JOUEUR = numJoueur * 17;
        int numeroJoueur = stringToInt(getCell(fichier, IDC_JOUEUR + 1, 0));
        int avancer = stringToInt(getCell(fichier, IDC_JOUEUR + 2, 0));
        boolean limitationVitesse;
        if(equals(getCell(fichier, IDC_JOUEUR + 3, 0), "true")){
            limitationVitesse = true;
        }else{
            limitationVitesse = false;
        }
        int nbQuestionLimitation = stringToInt(getCell(fichier, IDC_JOUEUR + 4, 0));
        String espaceVoiture = getCell(fichier, IDC_JOUEUR + 5, 0);
        return chargerJoueur(numeroJoueur, avancer, limitationVitesse, nbQuestionLimitation, espaceVoiture);
    }

    // fonction qui recréer le joueur avec toutes ses données sans les cartes 
    // dans sa main
    Joueur chargerJoueur(int numeroJoueur, int avancer, boolean limitationVitesse, int nbQuestionLimitation, String espaceVoiture){
        Joueur j = newJoueur(numeroJoueur);
        j.avancer = avancer;
        j.limitationVitesse = limitationVitesse;
        j.nbQuestionLimitation = nbQuestionLimitation;
        j.espaceVoiture = substring(espaceVoiture, 1, length(espaceVoiture)-1);
        return j;
    }

    // fonction qui récupére toutes les cartes d'un joueur pour les mettre dans sa main
    void recupererCartesJoueur(CSVFile fichier, Joueur joueur, int numJoueur){
        for(int numCarte = 0; numCarte < 6; numCarte++){
            final int IDC_JOUEUR_CARTE = numJoueur * 17 + numCarte * 2;
            String type = getCell(fichier, IDC_JOUEUR_CARTE + 6, 0);
            int borne = stringToInt(getCell(fichier, IDC_JOUEUR_CARTE + 7, 0));
            joueur.mainJoueur[numCarte] = rechercherTypeCarte(type, borne);
        }
    }

    // fonction qui va récupérer toutes les cartes de la pioche et l'indice de
    // la dernière carte distribué
    Pioche chargerPioche(){
        CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDE + "pioche.csv");
        Pioche pioche = newPioche();
        pioche.indiceDerniereCarte = stringToInt(getCell(fichier, 0, 0));
        for(int numCarte = 0; numCarte < 56; numCarte++){
            String type = getCell(fichier, 1+(numCarte*2), 0);
            int borne = stringToInt(getCell(fichier, 2+(numCarte*2), 0));
            pioche.pioche[numCarte] = rechercherTypeCarte(type, borne);
        }
        return pioche;
    }

    // fonction qui va rechercher le type de la carte et la créer
    Carte rechercherTypeCarte(String type, int borne){
        Carte carte;
        if(equals(type, "BORNE")){
            carte = newCarte(Type.BORNE, borne);
        }else if(equals(type, "LIMITATION_VITESSE")){
            carte = newCarte(Type.LIMITATION_VITESSE, borne);
        }else{
            carte = newCarte(Type.FIN_LIMITATION_VITESSE, borne);
        }
        return carte;
    }



    // fonction qui va charger des ressources pour l'affichage dans un tableau
    String[] chargerRessourceTableau(String cheminFichier){
        int nbLignes = nbLignesFichier(cheminFichier);
        String[] contenue = new String[nbLignes];
        extensions.File fichier = newFile(cheminFichier);
        int ligne = 0;
        while (ready(fichier)){
            contenue[ligne] = readLine(fichier);
            ligne++;
        }
        return contenue;
    }

    // fonction qui va charger des ressources pour l'affichage sans qu'il y 
    // est de retour à la ligne sur la dernère ligne
    String chargerRessourceSansRetourDerniereLigne(String cheminFichier){
        extensions.File fichier = newFile(CHEMIN_AFFICHAGE + cheminFichier);
        int nbLignes = nbLignesFichier(CHEMIN_AFFICHAGE + cheminFichier);
        String contenue = "";
        for(int numLigne = 1; numLigne <= nbLignes; numLigne++){
            contenue+= readLine(fichier);
            if(numLigne != nbLignes){
                contenue += NOUVELLE_LIGNE;
            }
        }        
        return contenue;
    }

    // fonction qui va charger des ressources pour l'affichage
    String chargerRessource(String cheminFichier){
        extensions.File fichier = newFile(CHEMIN_AFFICHAGE + cheminFichier);
        String contenue = "";
        while (ready(fichier)){
            contenue = contenue + readLine(fichier) + NOUVELLE_LIGNE;
        }
        return contenue;
    }

    int nbLignesFichier(String cheminFichier){
        extensions.File fichier = newFile(cheminFichier);
        int nbLignes = 0;
        while(ready(fichier)){
            readLine(fichier);
            nbLignes++;
        }
        return nbLignes;
    }

    // fonction qui affiche un contenue
    void afficherContenue(String contenue){
        print(contenue);
    }



    // fonction qui sert à récupérer les questions
    String[][] recupererQuestion(String nomFichier){
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS + nomFichier + ".csv", ';');
        String[][] question = new String[rowCount(fichier)][columnCount(fichier)];
        for(int numLigne = 0; numLigne < rowCount(fichier); numLigne++){
            for(int numColonne = 0; numColonne < columnCount(fichier); numColonne++){
                question[numLigne][numColonne] = getCell(fichier, numLigne, numColonne);
            }
        }
        return question;
    }



    // fonction pour créer un joueur
    Joueur newJoueur(int numeroJoueur){
        Joueur j = new Joueur();
        j.numeroJoueur = numeroJoueur;
        j.avancer = 0;
        j.mainJoueur = new Carte[6];
        j.limitationVitesse = false;
        j.nbQuestionLimitation = 0;
        j.espaceVoiture = "";
        j.voiture = chargerVoitureJoueur(numeroJoueur);
        return j;
    }

    String toString(Joueur j){
        String contenue = "";
        for(int numCarte = 0; numCarte<length(j.mainJoueur); numCarte++){
            contenue += toString(j.mainJoueur[numCarte]) + NOUVELLE_LIGNE;
        }

        return "Joueur numero " + j.numeroJoueur + NOUVELLE_LIGNE + 
        "A en main :" + NOUVELLE_LIGNE + contenue;
    }
    
    // fonction qui sert à charger la voiture d'un joueur
    String[] chargerVoitureJoueur(int numeroJoueur){
        extensions.File fichier = newFile(CHEMIN_AFFICHAGE + "voiture.txt");
        String[] contenue = new String[3];
        for(int indice = 0; indice < 3; indice++){
            String ligne = readLine(fichier);
            if(indice == 1){
                contenue[indice] = "  \\_/ J" + numeroJoueur + " \\_";
            }else{
                contenue[indice] = ligne;
            }
        }
        return contenue;
    }

    // fonction pour créer le tableau de joueurs
    Joueur[] creerJoueurs(int nbJoueur){
        Joueur[] listeJoueurs = new Joueur[nbJoueur];
        for(int i=0; i<nbJoueur; i++){
            listeJoueurs[i] = newJoueur(i+1);
        }
        return listeJoueurs;
    }

    // fonction qui sert à remplir la main des joueurs
    void remplirMainJoueurs(Joueur[] listeJoueurs, Pioche pioche){
        for(int emplacement = 0; emplacement < 6; emplacement++){
            for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
                joueurPioche(listeJoueurs[numJoueur], pioche, emplacement);
            }
        }
    }

    // fonction qui sert à faire piocher une carte à un joueur
    void joueurPioche(Joueur joueur, Pioche pioche, int emplacement){
        joueur.mainJoueur[emplacement] = pioche.pioche[pioche.indiceDerniereCarte];
        pioche.indiceDerniereCarte--;
    }



    // fonction pour créer la pioche
    Pioche newPioche(){
        Pioche p = new Pioche();
        int carteEnPioche = 0;
        p.pioche = new Carte[56];
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 4, Type.BORNE, 200);
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 12, Type.BORNE, 100);
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 10, Type.BORNE, 75);
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 10, Type.BORNE, 50);
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 10, Type.BORNE, 25);
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 4, Type.LIMITATION_VITESSE, 0);
        carteEnPioche = rajouterCarteDansPioche(p, carteEnPioche, 6, Type.FIN_LIMITATION_VITESSE, 0);

        p.indiceDerniereCarte = carteEnPioche-1;
        return p;
    }

    String toString(Pioche pioche){
        String contenue = "";
        for(int numCarte = 0; numCarte<pioche.indiceDerniereCarte; numCarte++){
            contenue += toString(pioche.pioche[numCarte]) + NOUVELLE_LIGNE;
        }
        return "Dans la pioche il y a :" + NOUVELLE_LIGNE + contenue;
    }

    // fonction qui sert à créer plusieurs fois la même carte
    int rajouterCarteDansPioche(Pioche p, int carteEnPioche, int nbCarte, Type type, int borne){
        for(int i = 0; i<nbCarte; i++, carteEnPioche++){
            p.pioche[carteEnPioche] = newCarte(type, borne);
        }
        return carteEnPioche;
    }

    // fonction qui sert à mélanger aléatoirement toutes les cartes de la pioche
    Pioche melangerPioche(Pioche pioche){
        int emplacementAleatoire;
        for(int numCarte = 0; numCarte < pioche.indiceDerniereCarte; numCarte++){
            Carte carte1 = pioche.pioche[numCarte];
            emplacementAleatoire = (int) (random()*pioche.indiceDerniereCarte);
            Carte carte2 = pioche.pioche[emplacementAleatoire];
            pioche.pioche[numCarte] = carte2;
            pioche.pioche[emplacementAleatoire] = carte1;
        }
        return pioche;
    }



    //
    boolean verifPeutPasJouerCarte(int numCarte, Joueur joueur, Joueur[] listeJoueurs){
        if(numCarte >= 0){
            if((joueur.avancer + joueur.mainJoueur[numCarte].borne) > 1000 || 
            (joueur.limitationVitesse && joueur.mainJoueur[numCarte].borne > 50) ||
            (!joueur.limitationVitesse && joueur.mainJoueur[numCarte].type == Type.FIN_LIMITATION_VITESSE) ||
            (joueur.mainJoueur[numCarte].type == Type.LIMITATION_VITESSE && tousJoueursLimite(listeJoueurs, joueur))){
                return true;
            }
        }
        return false;
    }

    // fonction pour vérifier si tous les joueurs non actif on une limitation de vitesse
    boolean tousJoueursLimite(Joueur[] listeJoueurs, Joueur joueur){
        for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
            if((numJoueur+1) != joueur.numeroJoueur && !listeJoueurs[numJoueur].limitationVitesse){
                return false;
            }
        }
        return true;
    }

    // fonction pour savoir quelle type de carte le joueur joue
    void joueurJoueCarte(Joueur joueur, int numCarte, Joueur[] listeJoueurs, int joueurActif, int numTour){
        if(joueur.mainJoueur[numCarte].type == Type.BORNE){
            joueurJoueCarteBorne(joueur, numCarte);
        }else if(joueur.mainJoueur[numCarte].type == Type.LIMITATION_VITESSE){
            saisieJoueurCarteMalus(joueur, listeJoueurs, joueurActif, numTour);
        }else{
            joueurJoueCarteBonus(joueur);
        }
    }

    // fonction pour faire avancer le joueur actif en point
    void joueurJoueCarteBorne(Joueur joueur, int numCarte){
        joueur.avancer += joueur.mainJoueur[numCarte].borne;
    }

    // fonction pour enlever la limitation de vitesse du joueur actif
    void joueurJoueCarteBonus(Joueur joueur){
        joueur.limitationVitesse = false;
        joueur.nbQuestionLimitation = 0;
    }

    // fonction pour faire défaussé une carte à un joueur et lui en piocher une nouvelle
    void joueurDefausseCarte(Joueur joueur, Pioche pioche, int numCarte){
        numCarte = (numCarte*-1)-1;
        joueurPioche(joueur, pioche, numCarte);
    }



    // fonction pour créer les cartes
    Carte newCarte(Type type, int borne){
        Carte c = new Carte();
        c.type = type;
        c.borne = borne;
        return c;
    }

    String toString(Carte c){
        String suite = "";
        if(c.type == Type.BORNE){
            suite = "de " + c.borne + " ";
        }
        return "Carte " + suite + c.type;
    }



    void testChangerJoueur(){
        assertEquals(changerJoueur(2, 1), 0);
        assertEquals(changerJoueur(2, 0), 1);
        assertEquals(changerJoueur(3, 2), 0);
        assertEquals(changerJoueur(4, 2), 3);
    }

    // fonction pour changer le joueur actif
    int changerJoueur(int nbJoueur, int joueurActif){
        return (joueurActif+1)%nbJoueur;
    }

    void testJoueurGagne(){
        Joueur j = newJoueur(-1);
        Joueur k = newJoueur(-1);
        k.avancer = 1000;
        Joueur m = newJoueur(-1);


        Joueur[] liste = new Joueur[]{j, m};
        Joueur[] liste2 = new Joueur[]{j, k};

        assertFalse(joueurGagne(liste));
        assertTrue(joueurGagne(liste2));
    }

    // fonction pour vérifier si aucun joueur n'à gagné
    boolean joueurGagne(Joueur[] listeJoueurs){
        for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
            if(listeJoueurs[numJoueur].avancer == 1000){
                return true;
            }
        }
        return false;
    }

    // fonction pour rechercher quel joueur à gagné la partie en ateignant 1000 points
    int rechercherJoueurAGagnePoint(Joueur[] listeJoueurs){
        int numJoueur=0;
        while(listeJoueurs[numJoueur].avancer != 1000){
            numJoueur++;
        }
        return listeJoueurs[numJoueur].numeroJoueur;
    }

    // fonction qui renvoie le numero du ou des joueur(s) qui ont gagné par manque de carte
    Joueur[] verifJoueurAGagneManqueCarte(Joueur[] listeJoueurs){
        // trouver le plus grand score
        int scorePlusGrand = listeJoueurs[0].avancer;
        for(int numJoueur = 1; numJoueur < length(listeJoueurs); numJoueur++){
            if(listeJoueurs[numJoueur].avancer > scorePlusGrand){
                scorePlusGrand = listeJoueurs[numJoueur].avancer;
            }
        }

        // trouver le nombre de joueur qui ont le plus grand score
        int nombreJoueurGagnat = 0;
        for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
            if(listeJoueurs[numJoueur].avancer == scorePlusGrand){
                nombreJoueurGagnat++;
            }
        }

        // les mettres dans la liste des gagnants
        int numJoueurGagnant = 0;
        Joueur[] listeGagnant = new Joueur[nombreJoueurGagnat];
        for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
            if(listeJoueurs[numJoueur].avancer == scorePlusGrand){
                listeGagnant[numJoueurGagnant] = listeJoueurs[numJoueur];
                numJoueurGagnant++;
            }
        }
        return listeGagnant;
    }

    // fonction qui renvoie le numero du joueur qui a gagné
    Joueur verifJoueurAGagnePoint(Joueur[] listeJoueurs){
        int numJoueur=0;
        while(listeJoueurs[numJoueur].avancer != 1000){
            numJoueur++;
        }
        return listeJoueurs[numJoueur];
    }




    // fonction pour afficher la partie haute de l'affichage avec
    // le numero du tour et l'avancer de tous les joueurs
    String afficherPointJoueur(Joueur[] listeJoueurs, int joueurActif, int numTour){
        String ligne = "Tour " + numTour + ": joueur " + (joueurActif+1) + NOUVELLE_LIGNE;
        ligne += HAUT_ROUTE;
        ligne += afficherVoitures(listeJoueurs);
        ligne += BAS_ROUTE;
        return ligne;
    }

    // fonction pour afficher les voitures de tous les joueurs
    String afficherVoitures(Joueur[] listeJoueurs){
        String voitures = "";
        for(int numJoueur = 0; numJoueur < length(listeJoueurs); numJoueur++){
            voitures += afficherVoiture(listeJoueurs[numJoueur]);
        }
        return voitures;
    }

    // fonction pour afficher la voiture d'un joueur
    String afficherVoiture(Joueur joueur){
        String voiture = "";
        for(int numLigne = 0; numLigne < length(joueur.voiture); numLigne++){
            voiture += joueur.espaceVoiture + joueur.voiture[numLigne] + NOUVELLE_LIGNE;
        }
        return voiture;
    }

    // fonction qui va lister dans l'ordre l'affichage des cartes du joueur pour pouvoir les affichers
    String[][] listeCartes(Joueur joueur){
        String[][] cartes = new String[6][11];
        for(int numCarte = 0; numCarte < length(joueur.mainJoueur); numCarte++){
            String[] carte = new String[11];
            Type typeCarte = joueur.mainJoueur[numCarte].type;
            if(typeCarte == Type.BORNE){
                int borneCarte = joueur.mainJoueur[numCarte].borne;
                if(borneCarte == 25){
                    carte = CARTE_25_BORNES;
                }else if(borneCarte == 50){
                    carte = CARTE_50_BORNES;
                }else if(borneCarte == 75){
                    carte = CARTE_75_BORNES;
                }else if(borneCarte == 100){
                    carte = CARTE_100_BORNES;
                }else if(borneCarte == 200){
                    carte = CARTE_200_BORNES;
                }
            }else if(typeCarte == Type.LIMITATION_VITESSE){
                carte = CARTE_LIMITATION;
            }else if(typeCarte == Type.FIN_LIMITATION_VITESSE){
                carte = CARTE_FIN_LIMITATION;
            }
            cartes[numCarte] = carte;
        }
        return cartes;
    }

    // fonction qui va afficher les cartes du joueur actif
    String afficherCarte(Joueur joueur){
        String contenue = "";
        String[][] cartes = listeCartes(joueur);
        for(int numColonne = 0; numColonne<length(cartes, 2); numColonne++){
            contenue += "          ";
            for(int numLigne=0; numLigne<length(cartes, 1); numLigne++){
                if(numColonne==5){
                    contenue += (numLigne+1) + ": ";
                }else{
                    contenue += "   ";
                }
                contenue += cartes[numLigne][numColonne] + "        ";
            }
            contenue += NOUVELLE_LIGNE;
        }
        return  contenue;
    }

    // fonction qui affiche la partie
    void afficher(Joueur[] listeJoueurs, int joueurActif, int numTour){
        println(afficherPointJoueur(listeJoueurs, joueurActif, numTour));
        int nbLignes = 25-3*length(listeJoueurs);
        if(listeJoueurs[joueurActif].limitationVitesse){
            nbLignes--;
        }
        for(int i=0; i<nbLignes;i++){
            println();
        }
        if(listeJoueurs[joueurActif].limitationVitesse){
            println("Vous êtes limiter à 50 bornes");
        }
        println(afficherCarte(listeJoueurs[joueurActif]));
    }

    // fonction pour afficher les régles du jeu
    void afficherRegle(){
        afficherContenue(REGLE_DU_JEU);
        readString();
    }

    // fonction pour annoncer le vainqeur de la partie
    void annonceVainqueur(Joueur[] listeJoueurs, Pioche pioche){
        if(pioche.indiceDerniereCarte == -1){
            Joueur[] listeGagnant = verifJoueurAGagneManqueCarte(listeJoueurs);
            afficherGagnantManqueCarte(listeGagnant);
        }else{
            Joueur gagnant = verifJoueurAGagnePoint(listeJoueurs);
            afficherGagnant(gagnant);
        }
    }

    // fonction pour afficher le joueur qui a atteint 1000 bornes
    void afficherGagnant(Joueur joueur){
        final String CHEMIN_AFFICHAGE_GAGNANT = CHEMIN_AFFICHAGE + "gagnant/";
        String[] annonceJoueur = chargerRessourceTableau(CHEMIN_AFFICHAGE_GAGNANT + "gagnant_singulier.txt");
        String[] numJoueur = chargerRessourceTableau(CHEMIN_AFFICHAGE_GAGNANT + joueur.numeroJoueur + ".txt");
        String espaceAvant = "";
        for(int nombreEspace = 0; nombreEspace < 15; nombreEspace++){
            espaceAvant += "   ";
            println();
        }
        for(int numLigne = 0; numLigne < length(annonceJoueur); numLigne++){
            print(espaceAvant + annonceJoueur[numLigne]);
            if(numLigne < 6){
                println(numJoueur[numLigne]);
            }else{
                println();
            }
            
        }
        println(FOND_GAGNANT);
    }

    // fonction qui va afficher le ou les joueur(s) gagnant(s) par manque de carte
    void afficherGagnantManqueCarte(Joueur[] listeGagnant){
        final String CHEMIN_AFFICHAGE_GAGNANT = CHEMIN_AFFICHAGE + "gagnant/";
        if(length(listeGagnant) == 1){
            afficherGagnant(listeGagnant[0]);
        }else{
            String cheminGagnant = CHEMIN_AFFICHAGE_GAGNANT + "gagnant_pluriels.txt";
            String[] annonceJoueur = chargerRessourceTableau(cheminGagnant);
            String[][] numeroJoueur = new String[length(listeGagnant)][nbLignesFichier(cheminGagnant)];
            String[] virgule = chargerRessourceTableau(CHEMIN_AFFICHAGE_GAGNANT + "virgule.txt");
            String[] et = chargerRessourceTableau(CHEMIN_AFFICHAGE_GAGNANT + "et.txt");
            for(int numJoueur = 0; numJoueur < length(listeGagnant); numJoueur++){
                numeroJoueur[numJoueur] = chargerRessourceTableau(CHEMIN_AFFICHAGE_GAGNANT + listeGagnant[numJoueur].numeroJoueur + ".txt");
            }
            println();
            for(int numLigne = 0; numLigne < length(annonceJoueur); numLigne++){
                print(annonceJoueur[numLigne]);
                for(int numJoueur = 0; numJoueur < length(listeGagnant); numJoueur++){
                    if(numLigne < 6){
                        print(numeroJoueur[numJoueur][numLigne]);
                    }else{
                        print("        ");
                    }
                    if(numJoueur < length(listeGagnant)-2){
                        print(virgule[numLigne]);
                        if(numLigne == 6){
                            print("  ");                        
                        }
                    }else if(numJoueur == length(listeGagnant)-2){
                        print(et[numLigne]);
                    }
                }
                println();
            }
            println(FOND_GAGNANT);
        }
    }

    // fonction pour changer l'espace avant la voiture d'un joueur
    void changerEspaceAvantVoiture(Joueur[] listeJoueurs, int joueurActif, int numTour){
        String espaceAvant = "";
        println(listeJoueurs[joueurActif].avancer);
        for(int i = 0; i < listeJoueurs[joueurActif].avancer/25; i++){
            espaceAvant += "    ";
        }
        while(!equals(espaceAvant, listeJoueurs[joueurActif].espaceVoiture)){
            listeJoueurs[joueurActif].espaceVoiture += " ";
            afficher(listeJoueurs, joueurActif, numTour);
            delay(125);
        }
    }

     // fonction pour afficher le menu de démarage
     boolean debutJeu(){
        String saisie;
        boolean chargerParti = false;
        do{
            afficherContenue(MENU);
            saisie = readString();
            if(equals(saisie, "h")){
                afficherRegle();
            }else if(equals(saisie, "charger")){
                chargerParti = true;
            }
        }while(!equals(saisie, ""));
        return chargerParti;
    }



    // fonction pour poser une question au joueur
    String affichageQuestion(String[][] QUESTIONS, int numQuestion){
        String question = "";
        for(int i=0; i<4; i++){
            if(i != 0){
                question += " " + i + ". ";
            }
            question += QUESTIONS[numQuestion][i];
            if(i == 1 || i == 2){
                question += " |";
            }
        }
        return question + " : ";
    }

    // fonction pour vérifier que la réponse saisie est correcte
    boolean reponseCorrecte(String[][] QUESTIONS, int numQuestion, int numReponseQuestion){
        return  equals(QUESTIONS[numQuestion][4], QUESTIONS[numQuestion][numReponseQuestion]);
    }

    // fonction pour poser les questions au joueur pour qu'il ne soit plus limité en vitesse
    void poserQuestionSiLimitation(Joueur[] listeJoueurs, int joueurActif, int numTour){
        boolean bonneReponse = true;
            while(listeJoueurs[joueurActif].nbQuestionLimitation > 0 && bonneReponse){
                int numQuestion = (int) (random()*length(QUESTIONS_NORMAL, 1));
                String texteQuestion = affichageQuestion(QUESTIONS_NORMAL, numQuestion);
                int reponseQuestion = saisieReponseQuestion(listeJoueurs, joueurActif, numTour, texteQuestion);
                if(reponseCorrecte(QUESTIONS_NORMAL, numQuestion, reponseQuestion)){
                    listeJoueurs[joueurActif].nbQuestionLimitation--;
                    if(listeJoueurs[joueurActif].nbQuestionLimitation == 0){
                        listeJoueurs[joueurActif].limitationVitesse = false;
                    }
                }else{
                    bonneReponse = false;
                }
                afficher(listeJoueurs, joueurActif, numTour);
            }
    }

    // fonction pour le tour du jeux du joueur et qui renvoie si la partie continue
    boolean tourJoueur(int joueurActif, int numeroCarteAJouer, Joueur[] listeJoueurs, Pioche pioche, int numTour){
        afficher(listeJoueurs, joueurActif, numTour);
        if(numeroCarteAJouer >= 0){
            if(listeJoueurs[joueurActif].mainJoueur[numeroCarteAJouer].borne == 200){
                int numQuestion = (int) (random()*length(QUESTIONS_DIFFICILE, 1));
                String texteQuestion = affichageQuestion(QUESTIONS_DIFFICILE, numQuestion);
                if(reponseCorrecte(QUESTIONS_DIFFICILE, numQuestion, saisieReponseQuestion(listeJoueurs, joueurActif, numTour, texteQuestion))){
                    joueurJoueCarte(listeJoueurs[joueurActif], numeroCarteAJouer, listeJoueurs, joueurActif, numTour);
                }
            }else{
                int numQuestion = (int) (random()*length(QUESTIONS_NORMAL, 1));
                String texteQuestion = affichageQuestion(QUESTIONS_NORMAL, numQuestion);
                if(reponseCorrecte(QUESTIONS_NORMAL, numQuestion, saisieReponseQuestion(listeJoueurs, joueurActif, numTour, texteQuestion))){
                    joueurJoueCarte(listeJoueurs[joueurActif], numeroCarteAJouer, listeJoueurs, joueurActif, numTour);
                }
            }
            joueurPioche(listeJoueurs[joueurActif], pioche, numeroCarteAJouer);
        }else{
            joueurDefausseCarte(listeJoueurs[joueurActif], pioche, numeroCarteAJouer);
        }
        changerEspaceAvantVoiture(listeJoueurs, joueurActif, numTour);
        boolean continuer = continuerOuQuitter(listeJoueurs, joueurActif, numTour, pioche);
        return continuer;
    }

    // fonction pour attendre le prochain joueur ou sauvegarder et quitter la partie
    boolean continuerOuQuitter(Joueur[] listeJoueurs, int joueurActif, int numTour, Pioche pioche){
        afficher(listeJoueurs, joueurActif, numTour);
        boolean continuer = true;
        String saisie;
        print("appuyer sur entrer pour continuer ou saisissez quitter pour sauvegarder : ");
        saisie = readString();
        if(equals(saisie, "quitter")){
            sauvegarder(numTour, listeJoueurs, pioche, joueurActif);
            continuer = false;
        }
        return continuer;
    }

    // fonction de la boucle principale du jeu
    void bouclePrincipale(Joueur[] listeJoueurs, Pioche pioche, int Tour, int jActif){
        int joueurActif = jActif;
        int numTour = Tour;
        int numeroCarteAJouer;
        boolean continuer = true;
        while(continuer && !joueurGagne(listeJoueurs) && pioche.indiceDerniereCarte > -1){
            afficher(listeJoueurs, joueurActif, numTour);
            poserQuestionSiLimitation(listeJoueurs, joueurActif, numTour);

            numeroCarteAJouer = saisieChoixCarte(listeJoueurs, joueurActif, numTour);
            continuer = tourJoueur(joueurActif, numeroCarteAJouer, listeJoueurs, pioche, numTour);
            joueurActif = changerJoueur(length(listeJoueurs), joueurActif);
            if(joueurActif == 0){
                numTour++;
            }
        }
        if(continuer == true){
            annonceVainqueur(listeJoueurs, pioche);
        }
    }



    // fonction de saisie pour un nombre entre un nombre minimum et maximum
    int saisieNombreMinMax(String contenue, char minimum, char maximum){
        String saisie;
        do{
            afficherContenue(contenue);
            saisie = readString();
            if(equals(saisie, "h")){
                afficherRegle();
            }
        }while(length(saisie) != 1 || charAt(saisie, 0) < minimum || charAt(saisie, 0) > maximum);
        return stringToInt(saisie);
    }

    // fonction de saisie pour que le joueur choisisse une carte
    int saisieChoixCarte(Joueur[] listeJoueurs, int joueurActif, int numTour){
        int numeroCarteAJouer;
        do{
            afficher(listeJoueurs, joueurActif, numTour);
            numeroCarteAJouer = verifCarteSelectionner(listeJoueurs[joueurActif], listeJoueurs, numTour);
        }while(verifPeutPasJouerCarte(numeroCarteAJouer, listeJoueurs[joueurActif], listeJoueurs));
        return numeroCarteAJouer;
    }

    // fonction pour vérifier la saisie de l'utilisateur
    int verifCarteSelectionner(Joueur joueur, Joueur[] listeJoueurs, int numTour){
        String saisie;
        do{
            afficher(listeJoueurs, joueur.numeroJoueur-1, numTour);
            print("Joueur " + joueur.numeroJoueur + ": Quelle carte jouer ? ");
            saisie = readString();
        }while(!verifSaisiePositive(saisie) && !verifSaisieNegative(saisie));
        int numCarte = stringToInt(saisie);
        if(numCarte>0){
            numCarte--;
        }
        return numCarte;
    }

    // fonction pour verifier si la valeur saisie est correcte pour la jouer
    boolean verifSaisiePositive(String saisie){
        return length(saisie) == 1 && charAt(saisie, 0) >= '1' && charAt(saisie, 0) <= '6';
    }

    // fonction pour verifier si la valeur saisie est correcte pour la défausser
    boolean verifSaisieNegative(String saisie){
        return length(saisie) == 2 && charAt(saisie, 0) == '-' && charAt(saisie, 1) >= '1' && charAt(saisie, 1) <= '6';
    }

    // fonction pour saisir a quel joueur on veut mettre une carte malus
    void saisieJoueurCarteMalus(Joueur joueur, Joueur[] listeJoueurs, int joueurActif, int numTour){
        String ligne = "A quel joueur voulais vous mettre ";
        ligne += texteLimitationVitesse(joueur, listeJoueurs);
        
        int numJoueurMalus;
        String saisie;
        do{
            do{
                afficher(listeJoueurs, joueurActif, numTour);
                print(ligne);
                saisie = readString();
            }while(length(saisie) != 1 && charAt(saisie, 0) < '2' || charAt(saisie, 0) > '4');

            numJoueurMalus = stringToInt(saisie);
        }while(verifJoueurMalus(listeJoueurs, joueur, numJoueurMalus));

        listeJoueurs[numJoueurMalus-1].limitationVitesse = true;
        listeJoueurs[numJoueurMalus-1].nbQuestionLimitation = 2;
    }

    // fonction pour faire le texte pour dire a quel joueur le joueur actif peut mettre une limitation de vitesse
    String texteLimitationVitesse(Joueur joueur, Joueur[] listeJoueurs){
        String ligne = "une limitation de vitesse :";
        for(int i=0; i<length(listeJoueurs); i++){
            if(listeJoueurs[i].numeroJoueur != joueur.numeroJoueur && !listeJoueurs[i].limitationVitesse){
                ligne += " Joueur " + listeJoueurs[i].numeroJoueur;
                if(i<length(joueur.mainJoueur)-1){
                    ligne += " | ";
            }
            }
        }
        return ligne;
    }

    // fonction pour verifier si le joueur saisie est correct
    boolean verifJoueurMalus(Joueur[] listeJoueurs, Joueur joueur, int numJoueurMalus){
        if(numJoueurMalus != joueur.numeroJoueur && !listeJoueurs[numJoueurMalus-1].limitationVitesse){
            return false;
        }
        return true;
    }

    int saisieReponseQuestion(Joueur[] listeJoueur, int JoueurActif, int numTour, String texteQuestion){
        String saisie;
        do{
            afficher(listeJoueur, JoueurActif, numTour);
            print(texteQuestion);
            saisie = readString();
        }while(!verifSaisieReponseCorrecte(saisie));
        return stringToInt(saisie);
    }

    boolean verifSaisieReponseCorrecte(String saisie){
        return length(saisie) == 1 && charAt(saisie, 0) >= '1' && charAt(saisie, 0) <= '3';
    }



    void algorithm(){
        Joueur[] listeJoueurs;
        Pioche pioche;
        boolean chargerPartie = debutJeu();
        int numTour = 1;
        int joueurActif = 0;

        if(!chargerPartie){
            listeJoueurs = creerJoueurs(saisieNombreMinMax(MENU_NB_JOUEUR, '2', '4'));
            pioche = melangerPioche(newPioche());
            remplirMainJoueurs(listeJoueurs, pioche);
        }else{
            int[] tabTour = chargerTour();
            numTour = tabTour[0];
            joueurActif = tabTour[1];
            listeJoueurs = chargerJoueurs();
            pioche = chargerPioche();
        }
        bouclePrincipale(listeJoueurs, pioche, numTour, joueurActif);
    }
}
