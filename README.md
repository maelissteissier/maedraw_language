# MAEDRAW Language

![maedraw example](maedraw_example.gif)

## Auteur

[Maëliss Teissier](https://github.com/maelissteissier)

## Description

Le langage maedraw est un langage déclaratif de dessin, typé statiquement et interprété, permettant de déclarer et déplacer des 
mines de crayons sur un canevas (une matrice de pixels). Le langage permet de changer leur couleur et leur taille de trait.

> Travail de session réalisé dans le cadre du cours INF600E - "Création de langages informatiques"


## Dépendances:
- **sablecc3.7 +** : pour générer le parser
- **maven** : pour lancer l'interpréteur

## Spécifications du language

Toute la spécification du language est détaillée dans le fichier PDF : [Rapport_et_specification.pdf](Rapport_et_specification.pdf)

La description formelle de la grammaire se trouve dans le fichier [maedraw.sablecc](maedraw.sablecc)

## Compiler l'interpréteur :
- Se rendre à la racine du projet
- Générer le parser avec sablecc à partir de la grammaire :
```sh
sablecc maedraw.sablecc -d src/main/java/
```
- Compiler l'interpréteur avec maven :
```sh
mvn compile
```

## Lancer l'interpréteur :
```sh
java  -classpath ./target/classes maedraw.Main <chemin_vers_program_principal.maedraw>
```

## Programmes de test fournis
VaryingTests/big_drawing.maedraw          
VaryingTests/dotted_line.maedraw          
VaryingTests/move_pos.maedraw             
VaryingTests/test_operations.maedraw      
VaryingTests/trygonometric_circle.maedraw

## Libraries fournies
VaryingTests/libraries/lib_color_palette.maedraw
VaryingTests/libraries/lib_shapes.maedraw

## Liste des commande pour lancer l'interprétation des fichiers tests:

```sh
java  -classpath ./target/classes maedraw.Main varyingTests/big_drawing.maedraw

java  -classpath ./target/classes maedraw.Main VaryingTests/dotted_line.maedraw

java  -classpath ./target/classes maedraw.Main VaryingTests/move_pos.maedraw

java  -classpath ./target/classes maedraw.Main VaryingTests/test_operations.maedraw

java  -classpath ./target/classes maedraw.Main VaryingTests/trygonometric_circle.maedraw
```

