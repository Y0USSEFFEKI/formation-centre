# Formation Centre - Documentation

## Vue d'ensemble

Application de gestion d'un centre de formation avec Spring Boot (backend) et Angular (frontend). Le système permet de gérer les étudiants, formateurs, cours, groupes, notes, séances et inscriptions.

## Architecture des données

### Tables et relations

#### 1. User (users)
- **Champs principaux**: id, username, password, fullname
- **Relations**:
  - One-to-One avec Etudiant
  - One-to-One avec Formateur
  - One-to-Many avec User_roles (rôles utilisateur)

#### 2. Etudiant (etudiants)
- **Champs principaux**: id, matricule, prenom, nom, email, dateNaissance
- **Relations**:
  - Many-to-One avec Groupe
  - One-to-One avec User
  - One-to-Many avec Note
  - One-to-Many avec Inscription

#### 3. Formateur (formateurs)
- **Champs principaux**: id, prenom, nom, email
- **Relations**:
  - One-to-One avec User
  - Many-to-Many avec Specialite (via formateur_specialite)
  - One-to-Many avec Cours
  - One-to-Many avec Seance

#### 4. Specialite
- **Champs principaux**: id, name
- **Relations**:
  - One-to-Many avec Cours
  - One-to-Many avec Groupe
  - Many-to-Many avec Formateur

#### 5. Cours (cours)
- **Champs principaux**: id, code, title, hours
- **Relations**:
  - Many-to-One avec Specialite
  - Many-to-One avec Formateur
  - One-to-Many avec Note
  - One-to-Many avec Inscription
  - One-to-Many avec Seance

#### 6. Groupe (groupes)
- **Champs principaux**: id, nom
- **Relations**:
  - Many-to-One avec Specialite
  - One-to-Many avec Etudiant
  - One-to-Many avec Seance

#### 7. Note (notes)
- **Champs principaux**: id, valeur, type
- **Relations**:
  - Many-to-One avec Etudiant
  - Many-to-One avec Cours

#### 8. Seance (seances)
- **Champs principaux**: id, heureDebut, heureFin, salle
- **Relations**:
  - Many-to-One avec Cours
  - Many-to-One avec Formateur
  - Many-to-One avec Groupe

#### 9. Inscription (inscriptions)
- **Champs principaux**: id, date
- **Relations**:
  - Many-to-One avec Etudiant
  - Many-to-One avec Cours

#### 10. User_roles
- **Champs principaux**: user_id, role
- Table de liaison pour les rôles des utilisateurs

## Interfaces utilisateur

### 1. Interface Administrateur

#### Gestion des Étudiants
- **Lister tous les étudiants** (`GET /admin/etudiants`)
- **Créer un étudiant** (`GET /admin/etudiants/create`, `POST /admin/etudiants/save`)
- **Modifier un étudiant** (`GET /admin/etudiants/edit/{id}`, `POST /admin/etudiants/save`)
- **Supprimer un étudiant** (via service)

#### Gestion des Formateurs
- **Lister tous les formateurs** (`GET /admin/formateurs`)
- **Créer un formateur** (`GET /admin/formateurs/create`, `POST /admin/formateurs/save`)
- **Modifier un formateur** (`GET /admin/formateurs/edit/{id}`, `POST /admin/formateurs/save`)
- **Supprimer un formateur** (via service)

#### Gestion des Cours
- **Lister tous les cours** (`GET /admin/cours`)
- **Créer un cours** (`GET /admin/cours/create`, `POST /admin/cours/save`)
- **Modifier un cours** (`GET /admin/cours/edit/{id}`, `POST /admin/cours/save`)
- **Supprimer un cours** (via service)

#### Gestion des Groupes
- **Lister tous les groupes** (`GET /admin/groupes`)
- **Créer un groupe** (`GET /admin/groupes/create`, `POST /admin/groupes/save`)
- **Modifier un groupe** (`GET /admin/groupes/edit/{id}`, `POST /admin/groupes/save`)
- **Supprimer un groupe** (via service)

#### Gestion des Spécialités
- **Lister toutes les spécialités** (`GET /admin/specialites`)
- **Créer une spécialité** (`GET /admin/specialites/create`, `POST /admin/specialites/save`)
- **Modifier une spécialité** (`GET /admin/specialites/edit/{id}`, `POST /admin/specialites/save`)
- **Supprimer une spécialité** (via service)

#### Gestion des Séances
- **Lister toutes les séances** (`GET /admin/seances`)
- **Créer une séance** (`GET /admin/seances/create`, `POST /admin/seances/save`)
- **Modifier une séance** (`GET /admin/seances/edit/{id}`, `POST /admin/seances/save`)
- **Supprimer une séance** (via service)

#### Gestion des Notes
- **Lister les notes par cours et groupe** (`GET /admin/notes`)
- **Créer une note** (`GET /admin/notes/create`, `POST /admin/notes/save`)
- **Modifier une note** (`GET /admin/notes/edit/{id}`, `POST /admin/notes/save`)
- **Supprimer une note** (via service)
- **Voir les notes d'un étudiant** (`GET /admin/notes/etudiant/{id}`)

#### Gestion des Inscriptions
- **Lister toutes les inscriptions** (`GET /admin/inscriptions`)
- **Créer une inscription** (`GET /admin/inscriptions/create`, `POST /admin/inscriptions/save`)
- **Modifier une inscription** (`GET /admin/inscriptions/edit/{id}`, `POST /admin/inscriptions/save`)
- **Supprimer une inscription** (via service)

### 2. Interface Étudiant

#### Profil et authentification
- **Connexion** (`POST /api/auth/login`)
- **Inscription** (`POST /api/auth/register`)
- **Voir son profil** (`GET /api/etudiant/me`)
- **Modifier son profil** (`PUT /api/etudiant/me`)

#### Catalogue et cours
- **Voir le catalogue des spécialités** (`GET /api/catalogue/specialites`)
- **Voir le catalogue des cours** (`GET /api/catalogue/cours`)
- **Voir ses cours inscrits** (`GET /api/etudiant/cours`)
- **Voir ses notes** (`GET /api/etudiant/notes`)
- **S'inscrire à un cours** (`POST /api/etudiant/inscriptions`)
- **Se désinscrire d'un cours** (`DELETE /api/etudiant/inscriptions/{coursId}`)

#### Emploi du temps
- **Voir son emploi du temps** (`GET /api/etudiant/emploi-du-temps`)

### 3. Interface Formateur

#### Profil et authentification
- **Connexion** (`POST /api/auth/login`)
- **Voir son profil** (`GET /api/formateur/me`)
- **Modifier son profil** (`PUT /api/formateur/me`)

#### Gestion des cours
- **Voir ses cours** (`GET /api/formateur/cours`)
- **Voir ses groupes** (`GET /api/formateur/groupes`)

#### Gestion des étudiants et notes
- **Voir la liste des étudiants d'un groupe avec leurs notes** (`GET /api/formateur/groupes/{groupeId}/etudiants/notes`)
- **Exporter la liste des étudiants et notes en PDF** (`GET /api/formateur/groupes/{groupeId}/etudiants/notes/pdf`)

#### Emploi du temps
- **Voir son emploi du temps** (`GET /api/formateur/emploi-du-temps`)

## Fonctionnalités transversales

### Calcul du taux de réussite
- Calcul automatique du taux de réussite d'un cours basé sur les notes (>= 10/20)
- Affichage de la moyenne générale du groupe pour un cours

### Génération de rapports PDF
- Export des listes d'étudiants avec notes par groupe et cours
- Format PDF avec tableau des étudiants, notes DS/Examen/Moyenne, statistiques

### Sécurité
- Authentification JWT
- Autorisation basée sur les rôles (ADMIN, FORMATEUR, ETUDIANT)
- Protection CSRF
- Gestion des sessions

### Catalogue public
- Consultation des spécialités et cours sans authentification
- Interface de navigation pour les futurs étudiants

## Technologies utilisées

### Backend
- **Spring Boot 4.0.0** avec Java 21
- **Spring Security** pour l'authentification et autorisation
- **Spring Data JPA** pour l'accès aux données
- **JWT** pour les tokens d'authentification
- **H2/MySQL** pour la base de données
- **Thymeleaf** pour les templates admin
- **Apache PDFBox** pour la génération de PDF

### Frontend
- **Angular** (standalone components)
- **TypeScript**
- **SCSS** pour le styling
- **RxJS** pour la programmation réactive

## Installation et exécution

### Prérequis
- Java 21
- Node.js et npm
- Maven

### Backend
```bash
cd jpa-spring-formation-centre
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd formation-frontend
npm install
npm start
```

L'application sera accessible sur :
- Backend API: http://localhost:8080
- Frontend: http://localhost:4200
- Interface admin: http://localhost:8080/admin