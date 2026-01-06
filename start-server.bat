@echo off
cd /d D:\Projet_stage_eranov_academie\Backend_ECarto\backend
mvn clean package -DskipTests -q
mvn spring-boot:run
