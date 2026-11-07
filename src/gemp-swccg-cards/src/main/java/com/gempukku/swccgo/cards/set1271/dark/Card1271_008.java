package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collection;
import java.util.List;

/** A Sith's Weapon, Virtual Block 7.1. */
public class Card1271_008 extends AbstractNormalEffect {
    public Card1271_008() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Sith's Weapon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Lightsaber confrontations are a complex dance of feints, strikes, parries and footwork. Mistakes are rarely forgiven.");
        setGameText("Deploy on table. Once per turn, if your character of ability > 4 just failed to 'hit' an [Episode I] Jedi (or [Virtual Block 6] Luke) with a non-[Permanent Weapon] lightsaber, may lose 1 Force to 'swing' at target again. May place in Used Pile to cancel Obi-Wan's Journal.");
        addIcons(Icon.EPISODE_I, Icon.SETUP, Icon.REFLECTIONS_III);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        if (!GameConditions.canTargetToCancel(game, self, Filters.ObiWans_Journal)) return null;
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId);
        CancelCardActionBuilder.buildCancelCardAction(action, Filters.ObiWans_Journal, Title.ObiWans_Journal);
        action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game,
            Effect effect, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.ObiWans_Journal)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.or(Filters.and(Icon.EPISODE_I, Filters.Jedi), Filters.and(Icon.LEGACY_BLOCK_6, Filters.Luke));
        Filter yourCharacter = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(4));
        Filter nonPermanentLightsaber = Filters.and(Filters.lightsaber, Filters.not(Icon.PERMANENT_WEAPON));
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) return null;
        final Collection<PhysicalCard> targets = TriggerConditions.getTargetsWeaponCardJustFailedToHit(game, effectResult,
                nonPermanentLightsaber, yourCharacter, targetFilter);
        if (targets.isEmpty()) return null;
        final PhysicalCard weapon = ((FiredWeaponResult) effectResult).getWeaponCardFired();
        if (!Filters.onTable.accepts(game, weapon)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
        action.setText("Lose 1 Force to 'swing' " + GameUtils.getFullName(weapon) + " again");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendCost(new LoseForceEffect(action, playerId, 1, true));
        action.appendEffect(new FireWeaponEffect(action, weapon, false, Filters.in(targets)) {
            @Override
            protected boolean isignorePerAttackOrBattleLimit() {
                return true;
            }
        });
        return List.of(action);
    }
}
