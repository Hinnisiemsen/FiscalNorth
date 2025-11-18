🏠 Haushaltsmanager (FiscalNorth)
Ein modernes, auf Spring Boot basierendes Backend zur Verwaltung persönlicher Finanzen. Diese Anwendung ermöglicht das Management von Bankkonten, Budgets, Verträgen und Transaktionen und integriert moderne KI-Funktionen zur Datenverarbeitung.

🚀 Features
Die Anwendung ist nach einer Package-by-Feature Architektur strukturiert und bietet folgende Kernfunktionalitäten:

Kontenverwaltung (/account):

Unterstützung verschiedener Kontotypen: Girokonto, Sparkonto, Krypto, Bargeld, Depot, PayPal, u.v.m.

Validierung von IBAN und BIC (ISO-Standard) beim Erstellen von Bankkonten.

Spezielle Logik für Festgeldkonten (DepositAccount) mit Zins- und Laufzeitverwaltung.

Transaktionsmanagement (/transaction):

Erfassung von Ausgaben, Einnahmen und Umbuchungen.

Kategorisierung und Tagging von Zahlungen.

Unterstützung für Split-Buchungen und Währungen (EUR, USD).

Vertragsmanagement (/contract):

Verwaltung wiederkehrender Zahlungen mit verschiedenen Intervallen (Täglich bis Jährlich).

Flag für automatisch erkannte Verträge (autoDetected).

Budgetierung (/budget):

Festlegen von Ausgabenlimits für bestimmte Zeiträume.

AI Integration 🤖:

Integration von Spring AI (Mistral AI) zur intelligenten Analyse.

PDF-Dokumentenanalyse (spring-ai-pdf-document-reader).

🛠 Tech Stack
Das Projekt nutzt moderne Java- und Spring-Technologien (Java 21 & Spring Boot 3.5.x Snapshot):

Core: Java 21, Spring Boot 3.5.7

Datenbank & Persistenz: Spring Data JPA, Hibernate.

API: Spring WebFlux (Reactive), Spring Data REST.

Messaging: RabbitMQ, Apache Kafka.

AI: Spring AI (Mistral AI Model).

Infrastruktur: Docker Compose (für RabbitMQ & Kafka).

Tools: Lombok, Maven.

⚙️ Voraussetzungen
Java 21 SDK

Docker & Docker Compose (für die Infrastruktur-Dienste)

🏃‍♂️ Starten der Anwendung
1. Repository klonen
Bash

git clone <repository-url>
cd FiscalNorth
2. Infrastruktur starten
Das Projekt beinhaltet eine compose.yaml für benötigte Dienste (z.B. RabbitMQ). Starte diese zuerst:

Bash

docker-compose up -d
Hinweis: RabbitMQ läuft standardmäßig auf Port 5672 (User: myuser, Pass: secret).

3. Anwendung bauen und starten
Nutze den Maven Wrapper, um die Anwendung ohne installieres Maven zu starten:

Windows:

DOS

./mvnw.cmd spring-boot:run
Mac/Linux:

Bash

./mvnw spring-boot:run
Die API ist anschließend unter http://localhost:8080 erreichbar.

📚 API Dokumentation
Das Projekt nutzt Standard REST-Controller. Hier sind einige wichtige Endpunkte:

Bankkonten:

GET /api/account/bank - Alle Konten abrufen

POST /api/account/bank - Neues Konto erstellen (benötigt IBAN/BIC Validierung)

Transaktionen:

GET /api/transaction/payment - Zahlungen abrufen

GET /api/transaction/transfer - Umbuchungen abrufen

User:

GET /api/user - Benutzerverwaltung

🧪 Testen
Das Projekt nutzt Testcontainers für Integrationstests, um eine echte Umgebung (Kafka, RabbitMQ) zu simulieren.

Bash

./mvnw test
Die Tests nutzen eine spezielle TestcontainersConfiguration, die automatisch Container für Kafka und RabbitMQ hochfährt.

📝 Konfiguration
Die Hauptkonfiguration befindet sich in src/main/resources/application.properties. Wichtige Standard-Einstellungen:

spring.jackson.mapper.accept-case-insensitive-enums=true (Erlaubt "checking" statt "CHECKING" im JSON).
