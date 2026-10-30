package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Imperial Academy Training
 */
public class Card1230_033 extends AbstractNormalEffect {
    public Card1230_033() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Academy_Training, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Graduates of the Empire's military academies train to fire standardissue weapons efficiently.");
        setGameText("Deploy on table. Imperials armed with a blaster or rifle at battleground sites are forfeit +2 and their weapon destiny draws are +1. Once per battle, if you have a non-lightsaber weapon there, may use 2 Force to add 4 to your total power. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter armedImps = Filters.and(Filters.your(self), Filters.Imperial, Filters.armedWith(Filters.or(Filters.blaster, Filters.rifle)), Filters.at(Filters.battleground_site));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, armedImps, 2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, armedImps, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_ACADEMY_TRAINING_V__ADD_POWER;
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber), Filters.participatingInBattle))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 4 to total power");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new TotalPowerModifier(self, Filters.battleLocation, 4, playerId),
                            "Adds 4 to total power"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
