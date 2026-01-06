-- Insertion de types de projet standards supplémentaires
-- Correction : Utilisation du nom de table correct 'type_projet' (singulier)
INSERT INTO type_projet (nom, description) VALUES
    ('Infrastructures Électriques', 'Projets liés à l''extension, la maintenance ou la modernisation du réseau électrique (HT/BT, postes, lignes).'),
    ('Adduction d''Eau Potable', 'Projets de construction de réseaux de distribution d''eau, forages, châteaux d''eau et stations de traitement.'),
    ('Télécoms & Fibre Optique', 'Déploiement d''infrastructures numériques, pose de fibre optique et construction de sites mobiles.'),
    ('Génie Civil & Bâtiment', 'Construction et rénovation de bâtiments administratifs, techniques ou industriels liés aux infrastructures.'),
    ('Énergie Renouvelable (Solaire)', 'Installation de parcs solaires, kits solaires domestiques et solutions d''énergie hybrides.');
