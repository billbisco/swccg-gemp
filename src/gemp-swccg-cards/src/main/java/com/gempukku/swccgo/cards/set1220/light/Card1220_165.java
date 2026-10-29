package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Starship
 * Subtype: Capital
 * Title: Bright Hope
 */
public class Card1220_165 extends AbstractCapitalStarship {
    public Card1220_165() {
        super(Side.LIGHT, 3, 3, 2, 4, null, 5, 5, Title.Bright_Hope, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Modified medium transport. Well armored. Has expanded passenger capacity to facilitate evacuation. The last transport to escape Hoth. Nearly destroyed by the Stalker.");
        setGameText("May add 2 pilots and 6 passengers. Permanent pilot provides ability of 2. During battle, if with a Rebel capital starship, may use 1 Force to cancel a just drawn battle destiny.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.MEDIUM_TRANSPORT);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(2);
        setPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isInBattleWith(game, self, Filters.Rebel_capital_starship)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canCancelDestiny(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel battle destiny");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
