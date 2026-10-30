package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilStartOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Echo Base Trooper Officer
 */
public class Card1104_006 extends AbstractRebel {
    public Card1104_006() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Echo Base Trooper Officer", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Like many Rebel specialists, Trey Callum defected from the Imperial officer corps. Trooper officers are masters at stretching the meager resources of the Rebellion.");
        setGameText("While at a non-shielded marker site, may use 1 Force to suspend 'Hoth Energy Shield Rules' until the start of your next turn.");
        addIcons(Icon.WARRIOR, Icon.HOTH);
        addKeywords(Keyword.ECHO_BASE_TROOPER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isAtLocation(game, self, Filters.and(Filters.marker_site, Filters.not(Filters.underHothEnergyShield())))
                && GameConditions.canUseForce(game, playerId, 1)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Suspend Hoth Energy Shield Rules");
            action.setActionMsg("Suspend 'Hoth Energy Shield Rules' until the start of " + playerId + "'s next turn");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new AddUntilStartOfPlayersNextTurnModifierEffect(action, playerId,
                            new SpecialFlagModifier(self, ModifierFlag.HOTH_ENERGY_SHIELD_RULES_SUSPENDED),
                            "Hoth Energy Shield Rules suspended until the start of " + playerId + "'s next turn"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
