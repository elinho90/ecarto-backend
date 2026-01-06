-- Insertion de données d'exemple pour les tests et la démonstration

-- Projets d'exemple
INSERT INTO projets (nom, description, statut, priorite, responsable, date_debut, date_fin_prevue, budget, progression, type_projet_id, tags) VALUES
    ('Migration Cloud AWS', 'Migration de l''infrastructure on-premise vers AWS avec architecture microservices. Optimisation des coûts et amélioration des performances.', 'EN_COURS', 'HAUTE', 'Sophie Martin', '2025-06-15', '2025-12-15', 250000, 65, 3, 'cloud,aws,migration,microservices'),
    ('Refonte SI RH', 'Refonte complète du système d''information des ressources humaines. Modernisation de l''interface et ajout de nouvelles fonctionnalités.', 'TERMINE', 'MOYENNE', 'Jean Dupont', '2025-01-10', '2025-05-30', 180000, 100, 1, 'rh,si,refonte,web'),
    ('Application Mobile CRM', 'Développement d''une application mobile pour la gestion de la relation client. Interface moderne et synchronisation en temps réel.', 'PREVU', 'HAUTE', 'Marie Dubois', '2025-07-01', '2025-11-30', 320000, 0, 2, 'mobile,crm,application,client'),
    ('Sécurisation Infrastructure', 'Mise en place d''un système de sécurité renforcé pour l''infrastructure existante. Audit de sécurité et implémentation des recommandations.', 'EN_COURS', 'HAUTE', 'Pierre Bernard', '2025-04-01', '2025-08-31', 150000, 40, 4, 'securite,infrastructure,audit,protection'),
    ('Data Warehouse', 'Construction d''un data warehouse pour l''analyse des données métier. Intégration de sources multiples et création de tableaux de bord.', 'EN_COURS', 'MOYENNE', 'Claire Laurent', '2025-03-15', '2025-09-15', 200000, 55, 5, 'data,warehouse,analytics,bi'),
    ('Portail Intranet', 'Refonte du portail intranet avec nouvelles fonctionnalités collaboratives. Amélioration de l''expérience utilisateur et mobile-first.', 'TERMINE', 'FAIBLE', 'Thomas Robert', '2024-11-01', '2025-02-28', 120000, 100, 1, 'intranet,collaboration,web,mobile'),
    ('API Gateway', 'Développement d''une API Gateway pour centraliser les accès aux microservices. Sécurité, monitoring et rate limiting.', 'EN_COURS', 'HAUTE', 'Sophie Martin', '2025-05-01', '2025-10-31', 175000, 30, 3, 'api,gateway,microservices'),
    ('Système de Backup', 'Mise en place d''un système de backup automatisé avec plan de reprise d''activité. Tests réguliers et monitoring.', 'PREVU', 'MOYENNE', 'Pierre Bernard', '2025-08-01', '2025-12-31', 95000, 0, 3, 'backup,recovery,automatisation'),
    ('Tableau de Bord BI', 'Création de tableaux de bord interactifs pour le pilotage de l''entreprise. Intégration avec le data warehouse.', 'EN_COURS', 'MOYENNE', 'Claire Laurent', '2025-04-15', '2025-09-30', 135000, 25, 5, 'bi,dashboard,reporting'),
    ('Formation Sécurité', 'Programme de formation à la sécurité pour tous les employés. Sensibilisation aux bonnes pratiques et tests de phishing.', 'TERMINE', 'FAIBLE', 'Marie Dubois', '2025-01-15', '2025-03-15', 45000, 100, 4, 'formation,securite,awareness');

-- Équipes des projets
INSERT INTO projet_equipe (projet_id, membre) VALUES
    (1, 'Sophie Martin'),
    (1, 'Jean Dupont'),
    (1, 'Marie Dubois'),
    (1, 'Pierre Bernard'),
    (2, 'Jean Dupont'),
    (2, 'Pierre Bernard'),
    (2, 'Claire Laurent'),
    (3, 'Marie Dubois'),
    (3, 'Thomas Robert'),
    (3, 'Sophie Martin'),
    (4, 'Pierre Bernard'),
    (4, 'Jean Dupont'),
    (5, 'Claire Laurent'),
    (5, 'Marie Dubois'),
    (5, 'Thomas Robert'),
    (6, 'Thomas Robert'),
    (6, 'Sophie Martin'),
    (7, 'Sophie Martin'),
    (7, 'Jean Dupont'),
    (8, 'Pierre Bernard'),
    (8, 'Claire Laurent'),
    (9, 'Claire Laurent'),
    (9, 'Marie Dubois'),
    (10, 'Marie Dubois'),
    (10, 'Pierre Bernard'),
    (10, 'Jean Dupont');

-- Rapports d'exemple
INSERT INTO rapports (nom, description, fichier_nom, fichier_type, fichier_taille, fichier_chemin, projet_id, uploade_par, faisabilite, risque, budget_estime, duree_estimee_mois, recommandations, analyse_automatique) VALUES
    ('Étude de Faisabilité - Migration Cloud AWS', 'Analyse complète de la faisabilité technique et financière de la migration vers AWS.', 'migration-aws-faisabilite.pdf', 'pdf', 2456789, '/uploads/reports/migration-aws-faisabilite.pdf', 1, 'Sophie Martin', 85, 'MOYEN', 250000, 6, 'Recommandations basées sur l''analyse automatique:\n\n✓ Projet hautement faisable - Recommandé pour implémentation\n⚠ Risque moyen - Plan de mitigation recommandé\n💰 Budget estimé: 250000€\n⏱️ Durée estimée: 6 mois\n\nDétails techniques:\n- Migration progressive recommandée\n- Tests de charge nécessaires\n- Formation équipe requise', true),
    
    ('Rapport d''Analyse - Refonte SI RH', 'État des lieux du système actuel et recommandations pour la refonte.', 'refonte-si-rh-analyse.pdf', 'pdf', 1823456, '/uploads/reports/refonte-si-rh-analyse.pdf', 2, 'Jean Dupont', 92, 'FAIBLE', 180000, 4, 'Recommandations basées sur l''analyse automatique:\n\n✓ Projet hautement faisable - Recommandé pour implémentation\n✓ Risque faible - Mise en œuvre standard\n💰 Budget estimé: 180000€\n⏱️ Durée estimée: 4 mois\n\nPoints clés:\n- Architecture modulaire recommandée\n- Migration des données à planifier\n- Formation utilisateurs essentielle', true),
    
    ('Spécifications Techniques - Application Mobile CRM', 'Spécifications détaillées et architecture technique de l''application mobile.', 'crm-mobile-specs.pdf', 'pdf', 3124567, '/uploads/reports/crm-mobile-specs.pdf', 3, 'Marie Dubois', 78, 'ELEVE', 320000, 5, 'Recommandations basées sur l''analyse automatique:\n\n⚠ Projet faisable avec réserves - Évaluation supplémentaire recommandée\n⚠ Risque élevé - Analyse approfondie et plan de contingence nécessaires\n💰 Budget estimé: 320000€\n⏱️ Durée estimée: 5 mois\n\nAspects critiques:\n- Sécurité des données renforcée nécessaire\n- Tests utilisateurs approfondis requis\n- Compatibilité multi-plateforme à valider', true),
    
    ('Audit de Sécurité - Infrastructure', 'Audit de sécurité complet avec recommandations d''amélioration.', 'audit-securite-infrastructure.pdf', 'pdf', 1567890, '/uploads/reports/audit-securite-infrastructure.pdf', 4, 'Pierre Bernard', 88, 'MOYEN', 150000, 3, 'Recommandations basées sur l''analyse automatique:\n\n✓ Projet hautement faisable - Recommandé pour implémentation\n⚠ Risque moyen - Plan de mitigation recommandé\n💰 Budget estimé: 150000€\n⏱️ Durée estimée: 3 mois\n\nPriorités:\n- Mise à jour des systèmes critiques\n- Formation équipe sécurité\n- Monitoring renforcé', true),
    
    ('Architecture Data - Data Warehouse', 'Conception de l''architecture data warehouse et modélisation des données.', 'data-warehouse-architecture.docx', 'docx', 2234567, '/uploads/reports/data-warehouse-architecture.docx', 5, 'Claire Laurent', 82, 'FAIBLE', 200000, 6, 'Recommandations basées sur l''analyse automatique:\n\n✓ Projet hautement faisable - Recommandé pour implémentation\n✓ Risque faible - Mise en œuvre standard\n💰 Budget estimé: 200000€\n⏱️ Durée estimée: 6 mois\n\nRecommandations:\n- Architecture scalable dès le départ\n- Outils ETL robustes\n- Governance des données à mettre en place', true),
    
    ('Plan de Secours - Reprise d''Activité', 'Plan complet de reprise d''activité en cas de sinistre.', 'plan-secours-pda.pdf', 'pdf', 1890345, '/uploads/reports/plan-secours-pda.pdf', 4, 'Pierre Bernard', 95, 'FAIBLE', 120000, 4, 'Recommandations basées sur l''analyse automatique:\n\n✓ Projet hautement faible - Recommandé pour implémentation\n✓ Risque faible - Mise en œuvre standard\n💰 Budget estimé: 120000€\n⏱️ Durée estimée: 4 mois\n\nPoints forts:\n- Plan robuste et testé\n- Procédures claires\n- Formation équipe incluse', true),
    
    ('Analyse ROI - Digitalisation Processus', 'Analyse du retour sur investissement pour la digitalisation des processus métiers.', 'roi-digitalisation.pdf', 'pdf', 2678901, '/uploads/reports/roi-digitalisation.pdf', 9, 'Claire Laurent', 73, 'ELEVE', 280000, 8, 'Recommandations basées sur l''analyse automatique:\n\n⚠ Projet faisable avec réserves - Évaluation supplémentaire recommandée\n⚠ Risque élevé - Analyse approfondie et plan de contingence nécessaires\n💰 Budget estimé: 280000€\n⏱️ Durée estimée: 8 mois\n\nConsidérations:\n- ROI positif à 3 ans\n- Changement management crucial\n- Formation utilisateurs intensive', true),
    
    ('Étude d''Impact - RGPD Compliance', 'Étude d''impact pour la conformité RGPD sur l''ensemble des systèmes.', 'rgpd-impact-study.pdf', 'pdf', 1456789, '/uploads/reports/rgpd-impact-study.pdf', 4, 'Marie Dubois', 89, 'MOYEN', 95000, 3, 'Recommandations basées sur l''analyse automatique:\n\n✓ Projet hautement faisable - Recommandé pour implémentation\n⚠ Risque moyen - Plan de mitigation recommandé\n💰 Budget estimé: 95000€\n⏱️ Durée estimée: 3 mois\n\nAspects réglementaires:\n- Conformité obligatoire\n- Documentation à jour\n- Formation équipe juridique', true);

-- Notifications d'exemple
INSERT INTO notifications (utilisateur_id, type, titre, message) VALUES
    (2, 'PROJET', 'Nouveau projet assigné', 'Vous avez été assigné comme responsable du projet "Migration Cloud AWS"'),
    (3, 'ECHEANCE', 'Échéance approchante', 'Le projet "Sécurisation Infrastructure" se termine dans 30 jours'),
    (4, 'RAPPORT', 'Nouveau rapport analysé', 'Le rapport "Spécifications Techniques - Application Mobile CRM" a été analysé automatiquement'),
    (5, 'BUDGET', 'Dépassement budgétaire', 'Le projet "Data Warehouse" a dépassé son budget de 15%'),
    (6, 'SUCCES', 'Projet terminé', 'Félicitations ! Le projet "Portail Intranet" a été terminé avec succès'),
    (7, 'RISQUE', 'Risque élevé détecté', 'Le projet "API Gateway" présente un risque élevé selon l''analyse automatique'),
    (2, 'FORMATION', 'Formation requise', 'Une formation AWS est recommandée pour le projet de migration'),
    (3, 'QUALITE', 'Audit qualité', 'Un audit qualité est planifié pour le projet "Refonte SI RH"'),
    (4, 'SECURITE', 'Vulnérabilité détectée', 'Une vulnérabilité de sécurité a été détectée dans l''infrastructure'),
    (5, 'PERFORMANCE', 'Performance dégradée', 'Les performances du data warehouse ont diminué de 20%');