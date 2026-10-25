package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateAttacksAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda
 */
public class Card1220_107 extends AbstractJediMaster {
    public Card1220_107() {
        super(Side.LIGHT, 1, 4, 3, 7, 9, "Yoda", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Help you I can, yes. For 800 years have I trained Jedi. Judge me by my size do you? Mm? And well you should not! For my ally is the Force... and a powerful ally it is.");
        setGameText("May deploy to Dagobah. Prevents attacks at same Dagobah site. Your training destiny draws are +1. Once per turn, may deploy (as if from hand) a Jedi Test from under Strong Is Vader. Once per game, may take The Way Of Things into hand from Reserve Deck; reshuffle. Immune to attrition.");
        addIcons(Icon.DAGOBAH);
        addPersona(Persona.YODA);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateAttacksAtLocationModifier(self, Filters.and(Filters.Dagobah_site, Filters.sameSite(self))));
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.and(Filters.your(self), Filters.Jedi_Test), 1));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployTestId = GameTextActionId.LEGACY__YODA_V__DEPLOY_JEDI_TEST;
        PhysicalCard strongIsVader = Filters.findFirstActive(game, self, Filters.title(Title.Strong_Is_Vader));
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, deployTestId)
                && strongIsVader != null
                && GameConditions.hasStackedCards(game, strongIsVader, Filters.Jedi_Test)) {
            final TopLevelGameTextAction deployTest = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, deployTestId);
            deployTest.setText("Deploy a Jedi Test from under Strong Is Vader");
            deployTest.appendUsage(
                    new OncePerTurnEffect(deployTest));
            deployTest.appendEffect(
                    new DeployStackedCardEffect(deployTest, strongIsVader, Filters.Jedi_Test, true, false));
            actions.add(deployTest);
        }

        GameTextActionId takeWayId = GameTextActionId.LEGACY__YODA_V__TAKE_THE_WAY_OF_THINGS;
        if (GameConditions.isOncePerGame(game, self, takeWayId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeWayId)) {
            final TopLevelGameTextAction takeWay = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, takeWayId);
            takeWay.setText("Take The Way Of Things into hand from Reserve Deck");
            takeWay.appendUsage(
                    new OncePerGameEffect(takeWay));
            takeWay.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(takeWay, playerId, Filters.title(Title.The_Way_Of_Things), true));
            actions.add(takeWay);
        }

        return actions;
    }
}
