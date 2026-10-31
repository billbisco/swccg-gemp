package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureWithImprisonmentEffect;
import com.gempukku.swccgo.logic.effects.DeliverCaptiveToPrisonEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Subtype: Immediate
 * Title: Bounty
 */
public class Card1112_026 extends AbstractImmediateEffect {
    public Card1112_026() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Bounty, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("One of the most profitable occupations in the galaxy is hunting down and capturing wanted beings. The more notable the quarry, the more profitable the venture.");
        setGameText("Deploy on a just captured character without a bounty. During your control phase, may transfer captive to a related prison (take this card into hand) to retrieve 4 Force. If released, place captive (and cards on them) in owner's Used Pile. Your Force drains are +1 here. (Immune to Control.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.BOUNTY, Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.captive, Filters.not(Filters.hasAttached(Filters.any_bounty)));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.character;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.captured(game, effectResult, Filters.and(Filters.character, Filters.not(Filters.hasAttached(Filters.any_bounty))))) {
            PhysicalCard captured = ((CaptureCharacterResult) effectResult).getCapturedCard();
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(captured), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        return getForceDrainModifiers(self);
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        return getForceDrainModifiers(self);
    }

    private List<Modifier> getForceDrainModifiers(PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getTransferCaptiveAction(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getTransferCaptiveAction(playerId, game, self, gameTextSourceCardId);
    }

    private List<TopLevelGameTextAction> getTransferCaptiveAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard captive = self.getAttachedTo();
        Filter prisonFilter = Filters.and(Filters.prison, Filters.relatedLocationTo(self, Filters.hasAttached(self)));
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && captive != null
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, prisonFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Transfer captive to a related prison");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose related prison", prisonFilter) {
                        @Override
                        protected void cardSelected(PhysicalCard prison) {
                            PhysicalCard escort = captive.getAttachedTo();
                            if (escort != null && Filters.character.accepts(game, escort)) {
                                action.appendEffect(
                                        new DeliverCaptiveToPrisonEffect(action, escort, captive, prison));
                            }
                            else {
                                action.appendEffect(
                                        new CaptureWithImprisonmentEffect(action, captive, prison, false, false));
                            }
                            action.appendEffect(
                                    new ReturnCardToHandFromTableEffect(action, self));
                            action.appendEffect(
                                    new RetrieveForceEffect(action, playerId, 4));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getReleasedTrigger(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getReleasedTrigger(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getReleasedTrigger(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.released(game, effectResult, Filters.hasAttached(self))) {
            PhysicalCard captive = self.getAttachedTo();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place captive in Used Pile");
            if (captive != null) {
                action.appendEffect(
                        new PlaceCardInUsedPileFromTableEffect(action, captive));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
