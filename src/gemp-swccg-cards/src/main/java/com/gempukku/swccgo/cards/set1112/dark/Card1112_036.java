package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToLimitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingEffect;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Device
 * Title: Laser Gate
 */
public class Card1112_036 extends AbstractDevice {
    public Card1112_036() {
        super(Side.DARK, 4, PlayCardZoneOption.BETWEEN_SITES, Title.Laser_Gate, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Security corridors are guarded by a grid of laser emplacements which can be activated upon demand to seal off sensitive areas from intrusion.");
        setGameText("Deploy between two non-exterior Death Star sites. Unless aboard a Lift Tube, for opponent's character to pass opponent must first lose 1 Force. At adjacent sites: weapon destiny draws are -1, your Force generation may not be limited, and opponent's characters are deploy +2.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    private static Filter eligibleSite() {
        return Filters.and(Filters.Death_Star_site, Filters.not(Filters.exterior_site));
    }

    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        return Filters.location;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        final Filter eligible = eligibleSite();
        return Filters.and(eligible, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(game, self, Filters.and(Filters.adjacentSite(physicalCard), eligible));
            }
        });
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return eligibleSite();
    }

    @Override
    protected List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, com.gempukku.swccgo.game.PlayCardOption playCardOption) {
        final Filter targetFilter = Filters.and(eligibleSite(), Filters.adjacentSite(target), Filters.not(target));
        TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose adjacent Death Star site", targetFilter) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard chosen) {
                action.addAnimationGroup(chosen);
                self.setTargetedCard(TargetId.EFFECT_TARGET_1, targetGroupId, chosen, targetFilter);
            }
        };
        return Collections.singletonList(targetingEffect);
    }

    private Filter boundingSites(PhysicalCard self) {
        return Filters.or(Filters.hasAttached(self), Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_1));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter sites = boundingSites(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.weapon, Filters.at(sites)), -1));
        modifiers.add(new ForceGenerationImmuneToLimitModifier(self, sites, Filters.any, self.getOwner()));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character), 2, sites));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter passer = Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.aboard(Filters.Lift_Tube)));
        Filter sites = boundingSites(self);
        if (TriggerConditions.movingFromLocation(game, effectResult, passer, sites)
                && TriggerConditions.movingToLocation(game, effectResult, passer, sites)) {
            String opponent = game.getOpponent(self.getOwner());
            PhysicalCard moving = ((MovingResult) effectResult).getCardMoving();
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose 1 Force to pass");
            action.setActionMsg("Make " + opponent + " lose 1 Force to pass " + GameUtils.getCardLink(self) + " with " + GameUtils.getCardLink(moving));
            action.appendEffect(new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
