package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Daughter Of Skywalker (V), final Light master page 7. */
public class Card1261_063 extends AbstractRebel {
    private static final GameTextActionId REFLECTION = GameTextActionId.LEGACY__DAUGHTER_OF_SKYWALKER_V__DEPLOY_REFLECTION;

    public Card1261_063() {
        super(Side.LIGHT, 1, 5, 4, 5, 8, Title.Daughter_Of_Skywalker, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scout. Leader. Made friends with Wicket. Negotiated an alliance with the Ewoks. Leia found out the truth about her father from Luke in the Ewok village.");
        setGameText("If in hand, may place her under Reserve Deck. May be targeted instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). May deploy Reflection from Reserve Deck; reshuffle. Immune to attrition < 4.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self,
            int gameTextSourceCardId) {
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText("Place under Reserve Deck");
        action.appendEffect(new PutCardFromHandOnBottomOfReserveDeckEffect(action, playerId, Filters.sameCardId(self), false));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self,
            int gameTextSourceCardId) {
        if (!GameConditions.canDeployCardFromReserveDeck(game, playerId, self, REFLECTION, Title.Reflection, true)) return null;
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, REFLECTION);
        action.setText("Deploy Reflection from Reserve Deck");
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Reflection), true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToAttritionLessThanModifier(self, 4));
    }
}
