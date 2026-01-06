package com.gs2e.stage_eranove_academy.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Script pour générer les hashes BCrypt de vos mots de passe
 * À EXÉCUTER UNE SEULE FOIS pour mettre à jour votre base de données
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Générer les hashs pour vos mots de passe actuels
        System.out.println("=== HASHS BCRYPT À COPIER DANS VOTRE BASE ===\n");

        // Pour chaque utilisateur, hasher son mot de passe
        String[] passwords = {
                "string", // Mot de passe actuel commun
                "admin123", // Si vous avez ce mot de passe
                "password" // Autres mots de passe possibles
        };

        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Mot de passe: " + password);
            System.out.println("Hash BCrypt: " + hash);
            System.out.println("---");
        }

        System.out.println("\n=== REQUÊTES SQL POUR METTRE À JOUR ===\n");

        // Générer les requêtes UPDATE
        String hashString = encoder.encode("string");

        System.out.println("-- Mettre à jour TOUS les utilisateurs avec le mot de passe 'string'");
        System.out.println("UPDATE utilisateurs SET password = '" + hashString + "';");
        System.out.println();

        System.out.println("-- OU mettre à jour individuellement :");
        System.out.println(
                "UPDATE utilisateurs SET password = '" + hashString + "' WHERE email = 'admin.systeme@ecarto.com';");
        System.out.println(
                "UPDATE utilisateurs SET password = '" + hashString + "' WHERE email = 'chef.projet@ecarto.com';");

        System.out.println("\n✅ Copiez ces requêtes et exécutez-les dans pgAdmin !");
    }
}