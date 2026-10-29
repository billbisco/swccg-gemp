package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.PresentInBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import java.util.LinkedList;
import java.util.List;



/**
 * Set: Reflections Gold
 * Type: Effect
 * Title: Close Air Support & Commando Training
 */
public class Card15_028 extends AbstractNormalEffect {
    public Card15_028() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Close Air Support & Commando Training", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        addComboCardTitles("Close Air Support", "Commando Training");
        setGameText("Deploy on table. While your Z-95 occupies a system or cloud sector, once per turn your scout at a related exterior site may add one battle destiny (if Z-95 is Tala 1 or Tala 2, may also add one destiny to total power only). Also, each of your non-unique Rebels armed with a non-unique blaster is forfeit +2, adds 1 to his total weapon destiny and, where present, cumulatively adds 1 to total battle destiny. (Immune to Alter while your non-unique blaster is on table.)");
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleAt(game, Filters.exterior_site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.scout))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            Filter z95Filter = Filters.and(Filters.your(self), Filters.Z_95, Filters.piloted, Filters.at(Filters.and(Filters.or(Filters.system, Filters.cloud_sector),
                    Filters.relatedLocationTo(self, Filters.battleLocation), Filters.occupies(playerId))));
            if (GameConditions.canSpot(game, self, z95Filter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add one battle destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddBattleDestinyEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                Filter talaFilter = Filters.and(Filters.your(self), Filters.Z_95, Filters.or(Filters.Tala_1, Filters.Tala_2),
                        Filters.piloted, Filters.at(Filters.and(Filters.or(Filters.system, Filters.cloud_sector),
                                Filters.relatedLocationTo(self, Filters.battleLocation), Filters.occupies(playerId))));
                if (GameConditions.canSpot(game, self, talaFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Add one battle destiny and one destiny to power");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new AddBattleDestinyEffect(action, 1));
                    action.appendEffect(
                            new AddDestinyToTotalPowerEffect(action, 1));
                    actions.add(action);
                }
            }
        }
        return actions;
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter armedRebelFilter = Filters.and(Filters.your(self), Filters.non_unique, Filters.Rebel, Filters.armedWith(Filters.and(Filters.non_unique, Filters.blaster)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, armedRebelFilter, 2));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, armedRebelFilter, 1));
        modifiers.add(new TotalBattleDestinyModifier(self, new DuringBattleAtCondition(Filters.wherePresent(self, armedRebelFilter)),
                new PresentInBattleEvaluator(self, armedRebelFilter), playerId));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OnTableCondition(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.blaster)), Title.Alter));
        return modifiers;
    }
}
