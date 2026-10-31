package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Character
 * Subtype: Republic
 * Title: Obi-Wan Kenobi, Jedi Knight
 */
public class Card1111_011 extends AbstractRepublic {
    public Card1111_011() {
        super(Side.LIGHT, 1, 7, 6, 6, 8, "Obi-Wan Kenobi, Jedi Knight", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Padawan learner promoted to Jedi Knight after his encounter with Darth Maul. Has sworn to train Anakin Skywalker, even if the Jedi Council forbids it.");
        setGameText("Deploys -1 to [Episode I] sites. During a battle (or duel) with a Dark Jedi, unless Obi-Wan's Journal on table, may use 1 Force to cancel a just drawn duel, weapon, or battle destiny. Once per game, may take Disarmed into hand from Reserve Deck; reshuffle. Immune to attrition < 5.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.and(Icon.EPISODE_I, Filters.site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__OBIWAN_JEDI_KNIGHT__TAKE_DISARMED;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Disarmed into hand from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Disarmed), true));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        if (!TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                || !GameConditions.canCancelDestiny(game, playerId)
                || GameConditions.canSpot(game, self, Filters.ObiWans_Journal)
                || !GameConditions.canUseForce(game, playerId, 1)) {
            return actions;
        }

        boolean duringBattleWithDarkJedi = GameConditions.isInBattleWith(game, self, Filters.Dark_Jedi)
                && (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isDuelDestinyJustDrawn(game, effectResult));
        boolean duringDuelWithDarkJedi = GameConditions.isDuringDuelWithParticipant(game, self)
                && GameConditions.isDuringDuelWithParticipant(game, Filters.Dark_Jedi)
                && TriggerConditions.isDuelDestinyJustDrawn(game, effectResult);

        if (duringBattleWithDarkJedi || duringDuelWithDarkJedi) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel opponent's destiny");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new CancelDestinyEffect(action));
            actions.add(action);
        }
        return actions;
    }
}
