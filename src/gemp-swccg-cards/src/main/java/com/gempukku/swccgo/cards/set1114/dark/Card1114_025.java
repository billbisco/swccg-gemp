package com.gempukku.swccgo.cards.set1114.dark;

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
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Scum and Villainy
 * Type: Starship
 * Subtype: Capital
 * Title: Dominator
 */
public class Card1114_025 extends AbstractCapitalStarship {
    public Card1114_025() {
        super(Side.DARK, 1, 6, 6, 5, null, 4, 6, Title.Dominator, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("Victory-class hull overhauled with powerful thrusters and latest hyperdrive technology. Engineered to support task forces combating Rebel starfighters.");
        setGameText("Destiny +2 when drawn for destiny. May add 2 pilots, 4 passengers, and 2 TIEs. Permanent pilot provides ability of 2. May reveal this starship from hand to place on Used Pile. Has ship-docking capability.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.DEATH_STAR_II);
        addModelType(ModelType.VICTORY_CLASS_STAR_DESTROYER);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setTIECapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText("Reveal to place in Used Pile");
        action.appendEffect(
                new ShowCardOnScreenEffect(action, self));
        action.appendEffect(
                new PutCardFromHandOnUsedPileEffect(action, playerId, self, false));
        return Collections.singletonList(action);
    }
}
