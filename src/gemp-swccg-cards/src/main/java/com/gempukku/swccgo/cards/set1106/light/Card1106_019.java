package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Starship
 * Subtype: Capital
 * Title: Redemption
 */
public class Card1106_019 extends AbstractCapitalStarship {
    public Card1106_019() {
        super(Side.LIGHT, 1, 3, 4, 5, null, 4, 6, Title.Redemption, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Nebulon-B frigate used as a mobile medical facility. Extra cargo space and weapon batteries have been modified to allow for more armor and more recovery areas.");
        setGameText("May add 4 pilots and 4 passengers. Permanent pilot provides ability of 2. Bacta Tank deploys for free and is lost if Redemption lost. X on Bacta Tank is -2.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_NEBULON_B_FRIGATE);
        setPilotCapacity(4);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeModifier(self, Filters.Bacta_Tank));
        modifiers.add(new CalculationVariableModifier(self, Filters.Bacta_Tank, -2, Variable.X));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)) {
            PhysicalCard bactaTank = Filters.findFirstActive(game, self, Filters.Bacta_Tank);
            if (bactaTank != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make Bacta Tank lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(bactaTank) + " lost");
                action.appendEffect(
                        new LoseCardFromTableEffect(action, bactaTank));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
