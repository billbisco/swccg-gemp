package com.gempukku.swccgo.cards.set2271.light;

import java.time.LocalDate;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.LiberatedSystemModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/** Massassi Base Sentry as on the PC website card list (PC-E2, by 2012-07-21), a version of 1271_40; replaced by the Virtual Cards Master List text (PC-E3, 2013-04-27). Same play. Anything Goes only (docs/legacy-versions.md). */
public class Card2271_2040 extends AbstractNormalEffect {
    private static final Filter OTHER_LIBERATED_SYSTEM = Filters.and(LegacyFilters.liberatedSystem, Filters.not(Filters.Yavin_4_system));
    private static final Filter WARRIOR_IN_BATTLE = Filters.and(Filters.Rebel, Filters.warrior, Filters.participatingInBattle);

    public Card2271_2040() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Massassi Base Sentry", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Rebel sentries are stationed on raised sensor platforms. On watch for Imperial scouts and other hazards, they supplement data gathered by Yavin Base's main sensors.");
        setGameText("Deploy on Yavin 4; it is 'liberated.' At sites related to other 'liberated' systems, may add 2 to power of each Rebel warrior (to a maximum of 4). Once per turn, while you control this system, may retrieve 1 Force when you Force drain at another 'liberated' system.");
        setLegacyVersion(new LegacyVersion("1271_40", "PC-E2",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 13)));
        addIcons(Icon.SETUP);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Yavin_4_system;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.Yavin_4_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        PhysicalCard yavin = self.getAttachedTo();
        return yavin == null ? List.of() : List.of(new LiberatedSystemModifier(self, yavin));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        Filter eligibleSite = Filters.and(Filters.site, Filters.relatedLocationTo(self, OTHER_LIBERATED_SYSTEM));
        if (!GameConditions.isDuringBattleAt(game, eligibleSite)
                || !GameConditions.isOncePerBattle(game, self, playerId, sourceId)
                || !GameConditions.canSpot(game, self, Filters.and(WARRIOR_IN_BATTLE, Filters.powerLessThan(4)))) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Add up to 2 power to each Rebel warrior");
        action.appendUsage(new OncePerBattleEffect(action));
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                for (PhysicalCard card : Filters.filterActive(game, self, WARRIOR_IN_BATTLE)) {
                    float increase = Math.min(2, 4 - game.getModifiersQuerying().getPower(game.getGameState(), card));
                    if (increase > 0) {
                        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                            new PowerModifier(self, Filters.and(card, Filters.participatingInBattle), increase), null));
                    }
                }
            }
        });
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        PhysicalCard yavin = self.getAttachedTo();
        if (yavin == null || !Filters.controls(playerId).accepts(game, yavin)
                || !TriggerConditions.forceDrainInitiatedBy(game, result, playerId, OTHER_LIBERATED_SYSTEM)
                || !GameConditions.isOncePerTurn(game, self, playerId, sourceId)
                || !GameConditions.hasLostPile(game, playerId)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, sourceId);
        action.setText("Retrieve 1 Force with Massassi Base Sentry");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
        return List.of(action);
    }
}
