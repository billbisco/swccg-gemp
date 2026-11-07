package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Death Star: War Room, Virtual Block 7.1. */
public class Card1271_023 extends AbstractSite {
    private static final String WIPE_THEM_OUT = "Wipe Them Out, All Of Them";
    private static final Filter BLOCK_FIVE_WIPE_THEM_OUT = Filters.and(Filters.title(WIPE_THEM_OUT), Icon.LEGACY_BLOCK_5);

    public Card1271_023() {
        super(Side.DARK, Title.Death_Star_War_Room, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If Alderaan 'blown away,' your movement to and from Death Star system is free, and at Death Star sites, your Force drains and battle damage against you are canceled.");
        setLocationLightSideGameText("This site is never a battleground. You may not limit Force generation at mobile locations (except Executor sites). [Virtual Block 5] Wipe Them Out, All Of Them is canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String darkPlayer, SwccgGame game, PhysicalCard self) {
        Condition alderaanBlownAway = (state, modifiers) -> GameConditions.isBlownAway(game, Filters.Alderaan_system);
        return List.of(
            new MovesFreeFromLocationModifier(self, Filters.owner(darkPlayer), alderaanBlownAway, Filters.Death_Star_system),
            new MovesFreeToLocationModifier(self, Filters.owner(darkPlayer), alderaanBlownAway, Filters.Death_Star_system),
            new NoBattleDamageModifier(self, Filters.Death_Star_site, alderaanBlownAway, darkPlayer));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String darkPlayer,
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.forceDrainInitiatedBy(game, result, darkPlayer, Filters.Death_Star_site)
                || !GameConditions.isBlownAway(game, Filters.Alderaan_system)
                || !GameConditions.canCancelForceDrain(game, darkPlayer, self)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setPerformingPlayer(darkPlayer);
        action.setText("Cancel Force drain at Death Star site");
        action.appendEffect(new CancelForceDrainEffect(action));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String lightPlayer, SwccgGame game, PhysicalCard self) {
        Filter mobileLocationsExceptExecutorSites = Filters.and(Filters.location, Icon.MOBILE, Filters.not(Filters.Executor_site));
        return List.of(new NonBattlegroundModifier(self, self),
            new ForceGenerationImmuneToLimitModifier(self, mobileLocationsExceptExecutorSites, Filters.owner(lightPlayer)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredBeforeTriggers(String lightPlayer,
            SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, BLOCK_FIVE_WIPE_THEM_OUT)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setPerformingPlayer(lightPlayer);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String lightPlayer,
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)
                || !GameConditions.canTargetToCancel(game, self, BLOCK_FIVE_WIPE_THEM_OUT)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setPerformingPlayer(lightPlayer);
        CancelCardActionBuilder.buildCancelCardAction(action, BLOCK_FIVE_WIPE_THEM_OUT, WIPE_THEM_OUT);
        return List.of(action);
    }
}
