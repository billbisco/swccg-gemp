package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerCaptiveEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayEscortCaptivesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Mission
 * Title: In The Hands Of The ISB
 */
public class Card1271_027 extends AbstractMission {
    public Card1271_027() {
        super(Side.DARK, 2, PlayCardZoneOption.ATTACHED, "In The Hands Of The ISB", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V, false);
        setGameText("Deploy on ISB Central Office. This site is a prison (if opponent controls, all captives here are released). Total ability of 6 or more required for opponent to draw battle destiny here. Whenever an ISB agent delivers a captive here, opponent loses 4 Force (once per captive per captivity). ISB agents may escort captives and, if escorting a captive during your move phase, may use 1 Force to relocate escort and captive to a related docking bay (as a regular move). Once during your move phase, may use 2 Force to relocate (as a regular move) an ISB agent present with Ysanne Isard here to a site you control.");
        addIcons(Icon.SETUP, Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.titleContains("ISB Central Office");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.titleContains("ISB Central Office");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter thisSite = Filters.here(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, thisSite, Keyword.PRISON));
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, thisSite, 6, opponent));
        modifiers.add(new MayEscortCaptivesModifier(self, Filters.and(Filters.your(self), Filters.ISB_agent)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, Filters.here(self))
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.here(self)))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Release captives");
            action.appendEffect(new ReleaseCaptivesEffect(action, Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.here(self)))));
            actions.add(action);
        }
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IN_THE_HANDS_OF_THE_ISB__DELIVER_CAPTIVE;
        if (TriggerConditions.captiveDeliveredToPrisonBy(game, effectResult, Filters.ISB_agent, Filters.here(self))) {
            PhysicalCard captive = ((DeliveredCaptiveToPrisonResult) effectResult).getCaptive();
            if (GameConditions.isOncePerCaptive(game, self, captive, gameTextActionId)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose 4 Force");
                action.appendUsage(new OncePerCaptiveEffect(action, captive));
                action.appendEffect(new LoseForceEffect(action, opponent, 4));
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 2)) {
            Filter isbWithIsardHere = Filters.and(Filters.your(self), Filters.ISB_agent, Filters.here(self),
                    Filters.presentWith(self, Filters.title("Ysanne Isard")));
            Filter siteYouControl = Filters.and(Filters.site, Filters.controls(playerId));
            if (GameConditions.canTarget(game, self, isbWithIsardHere)
                    && GameConditions.canSpotLocation(game, siteYouControl)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
                action.setText("Relocate ISB agent to a site you control");
                action.appendUsage(new OncePerPhaseEffect(action));
                action.appendCost(new UseForceEffect(action, playerId, 2));
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose ISB agent", isbWithIsardHere) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard agent) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose site you control", siteYouControl) {
                                            @Override
                                            protected void cardTargeted(int siteGroupId, final PhysicalCard site) {
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(agent),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                action.appendEffect(new RelocateBetweenLocationsEffect(action, agent, site, true));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 1)) {
            Filter escorting = Filters.and(Filters.your(self), Filters.ISB_agent, Filters.escorting(Filters.captive));
            Filter relatedDockingBay = Filters.and(Filters.docking_bay, Filters.relatedSiteTo(self, escorting));
            if (GameConditions.canTarget(game, self, escorting)
                    && GameConditions.canSpotLocation(game, relatedDockingBay)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
                action.setText("Relocate escort and captive to related docking bay");
                action.appendCost(new UseForceEffect(action, playerId, 1));
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose escorting ISB agent", escorting) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard escort) {
                                Filter bay = Filters.and(Filters.docking_bay, Filters.relatedSite(escort));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose related docking bay", bay) {
                                            @Override
                                            protected void cardTargeted(int bayGroupId, final PhysicalCard bayCard) {
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(escort),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                action.appendEffect(new RelocateBetweenLocationsEffect(action, escort, bayCard, true));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }
}
