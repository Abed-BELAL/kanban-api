# Kanban API

API REST pour une application de Kanban Board (utilisateurs, listes, cartes), réalisée dans le cadre d'un exercice de veille technologique backend. Technologie choisie : **Spring Boot** (comparatif et justification dans `rapport-veille-back.pdf`).

## Stack technique

- Java 25
- Spring Boot 4.1.1 (Web, Data JPA, Security, Validation)
- PostgreSQL 16
- Spring Security OAuth2 JOSE (JWT stateless, HS256)
- springdoc-openapi (documentation Swagger)
- Lombok

## Prérequis

- JDK 25
- Docker (pour la base PostgreSQL) ou une instance PostgreSQL locale
- Le Maven Wrapper est fourni (`mvnw` / `mvnw.cmd`), pas besoin d'installer Maven

## Lancer le projet

1. Démarrer la base de données :

   ```bash
   docker-compose up -d
   ```

   Cela lance un PostgreSQL 16 sur le port 5432 (base `kanban`, utilisateur `kanban`, mot de passe `kanban`).

2. Démarrer l'API :

   ```bash
   ./mvnw spring-boot:run      # Linux / macOS
   mvnw.cmd spring-boot:run    # Windows
   ```

   Le serveur démarre sur `http://localhost:3000`.

3. Ouvrir la documentation Swagger : [http://localhost:3000/api](http://localhost:3000/api)
   (redirige vers l'interface Swagger UI, qui liste toutes les routes du contrat `openapi.yaml`).

## Variables d'environnement

Toutes ont une valeur par défaut adaptée au `docker-compose.yml` fourni ; à surcharger en production.

| Variable | Rôle | Défaut |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/kanban` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur base de données | `kanban` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe base de données | `kanban` |
| `JWT_SECRET` | Clé HMAC de signature des JWT (32 caractères minimum) | valeur de dev fournie, **à changer en production** |

## Authentification

L'API est protégée par JWT (stateless, pas de session). Le token s'obtient via `/api/auth/login` et se transmet ensuite dans l'en-tête `Authorization: Bearer <token>` de chaque requête protégée.

```bash
# 1. Inscription
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"ada@example.com","password":"motdepasse","name":"Ada"}'

# 2. Connexion : récupère un accessToken
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ada@example.com","password":"motdepasse"}'

# 3. Appel authentifié
curl http://localhost:3000/api/lists \
  -H "Authorization: Bearer <accessToken>"
```

Dans Swagger UI, cliquer sur le bouton **Authorize** et coller le token (sans le préfixe `Bearer`, Swagger l'ajoute automatiquement).

Il n'y a pas de jeu de données préchargé (pas de fixtures au démarrage) : il faut d'abord créer un compte via `/api/auth/register` avant de pouvoir tester le reste de l'API.

## Modèle de données

- **User** : `id`, `email` (unique), `password` (hash BCrypt, jamais exposé par l'API), `name`, `role` (`user` ou `admin`), `createdAt`
- **KanbanList** : `id`, `title`, `position`, `ownerId`, `createdAt`
- **Card** : `id`, `title`, `description`, `position`, `listId`, `createdAt`, `updatedAt`

## Routes

Toutes les routes sont préfixées par `/api`.

### Authentification & utilisateurs

| Méthode | Route | Auth requise | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Non | Inscription. 201, ou 409 si email déjà utilisé, 400 si payload invalide |
| POST | `/api/auth/login` | Non | Connexion, renvoie `{ "accessToken": "<JWT>" }`. 401 générique si identifiants invalides |
| GET | `/api/users/me` | Oui | Profil de l'utilisateur connecté |
| PATCH | `/api/users/{id}` | Oui | Modifie son propre profil. 403 si on modifie le profil d'un autre, ou si on tente de changer son propre `role` sans être admin |

### Listes

| Méthode | Route | Auth requise | Description |
|---|---|---|---|
| GET | `/api/lists` | Oui | Liste les listes de l'utilisateur connecté uniquement |
| POST | `/api/lists` | Oui | Crée une liste, l'utilisateur courant en devient l'auteur |
| PATCH | `/api/lists/{id}` | Oui + auteur | Modifie une liste. 403 si on n'en est pas l'auteur |
| DELETE | `/api/lists/{id}` | Oui + auteur | Supprime une liste (voir choix assumé ci-dessous) |

### Cartes

| Méthode | Route | Auth requise | Description |
|---|---|---|---|
| GET | `/api/lists/{listId}/cards` | Oui + auteur de la liste | Liste les cartes d'une liste |
| POST | `/api/lists/{listId}/cards` | Oui + auteur de la liste | Crée une carte dans la liste |
| GET | `/api/cards/{id}` | Oui + auteur de la liste parente | Récupère une carte |
| PATCH | `/api/cards/{id}` | Oui + auteur de la liste parente (et de la liste cible en cas de déplacement) | Modifie une carte, y compris son `listId` pour la déplacer |
| DELETE | `/api/cards/{id}` | Oui + auteur de la liste parente | Supprime une carte |

## Choix assumés

- **403 plutôt que 404 sur les ressources d'autrui** : agir sur une liste ou une carte dont on n'est pas l'auteur renvoie 403 Forbidden (pas 404), pour être explicite sur le refus de droits.
- **Suppression en cascade** : supprimer une liste (`DELETE /api/lists/{id}`) supprime aussi toutes ses cartes. Il n'y a pas de refus si la liste n'est pas vide.
- **Changement de rôle réservé aux admins** : un utilisateur qui tente de modifier son propre champ `role` via `PATCH /api/users/{id}` reçoit 403, même sur son propre profil. Seul un compte `admin` peut changer le rôle d'un utilisateur.
- **Mot de passe jamais exposé** : aucune réponse API (register, login, users/me, PATCH users) ne renvoie le champ `password`, en clair ou haché.

## Documentation de veille

Le comparatif des trois technologies étudiées (NestJS, Symfony, Spring Boot) et la justification du choix de Spring Boot sont dans [`rapport-veille-back.pdf`](./rapport-veille-back.pdf).
