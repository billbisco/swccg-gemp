package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Han, Chewie, And The Falcon (V), final Virtual Block 6.1 text. */
public class Card1261_103 extends AbstractStarfighter {
    public Card1261_103() {
        super(Side.LIGHT, 2, 6, 8, null, 5, 7, 6, "Han, Chewie, And The Falcon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setComboCard(true);
        setLore("Although temperamental, this trusty hunk of junk always seems to perform for its proud owner and his Wookiee co-pilot when needed the most.");
        setGameText("Permanent pilots are •Han and •Chewie, who provide ability of 5. Life Debt is canceled. Once per game, may take A Few Maneuvers into hand from Reserve Deck; reshuffle. Immune to Come With Me, Lateral Damage, and attrition < 5.");
        addPersona(Persona.FALCON);
        addIcons(Icon.REFLECTIONS_III, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 2);
        addModelType(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Arrays.asList(new AbstractPermanentPilot(Persona.HAN, 3) {}, new AbstractPermanentPilot(Persona.CHEWIE, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Arrays.asList(new ImmuneToTitleModifier(self, Title.Come_With_Me),
                new ImmuneToTitleModifier(self, Title.Lateral_Damage),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Life_Debt)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self,
            int gameTextSourceCardId) {
        GameTextActionId actionId = GameTextActionId.LEGACY__HAN_CHEWIE_AND_THE_FALCON__TAKE_A_FEW_MANEUVERS;
        if (GameConditions.isOncePerGame(game, self, actionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, actionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, actionId);
            action.setText("Take A Few Maneuvers into hand from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.A_Few_Maneuvers, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
