Dans un restaurant, les clients peuvent choisir un plat en remplissant un formulaire dans lequel ils peuvent sélectionner les ingrédients à inclure ou non. Les choix d'ingrédients disponibles sont : tomates, oignons et crevettes. Les plats qui peuvent être préparés à partir de ces ingrédients sont des pizzas ou des nouilles. Le restaurant applique une stratégie d'attribution des plats en fonction de critères spécifiques. Il existe trois stratégies d'attribution :

1) Toujours attribuer des pizzas.
2) Toujours attribuer des nouilles.
3) Si le plat contient des tomates, attribuer une pizza, sinon attribuer des nouilles.

L'interface graphique est composée d'une seule fenêtre contenant les éléments suivants : trois cases à cocher pour les choix d'ingrédients (tomates, oignons, crevettes), trois boutons radio (PizzaForever, NoodlesForever, ThinkAboutTomato), un bouton qui, une fois cliqué, affiche le plat déterminé par la stratégie d'attribution sur un label, et un bouton de réinitialisation pour permettre à l'utilisateur de recommencer ses choix.

Le programme est architecturé selon les design patterns du MVC (Modèle-Vue-Contrôleur), de la Factory et de la Strategy. Le contrôleur gère les événements provenant de la vue et met à jour celle-ci en conséquence. La Factory est responsable de retourner le plat attribué en fonction de la stratégie d'attribution. La Strategy est utilisée au sein de la Factory pour permettre des changements dynamiques de la stratégie d'attribution.
