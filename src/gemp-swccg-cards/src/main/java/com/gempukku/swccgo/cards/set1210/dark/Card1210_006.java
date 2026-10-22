package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Danz Borin
 */
public class Card1210_006 extends AbstractAlien {
    public Card1210_006() {
        super(Side.DARK, 3, 2, 2, 2, 4, Title.Danz_Borin, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Cocky gunner and bounty hunter. Maintains a residence on Nar Shaddaa, the spaceport moon of the Hutt homeworld. To his companions' delight, he's nearly as good as he boasts.");
        setGameText("Adds 3 to power of anything he pilots. May add 1 to Force drains at same Nal Hutta location. Danz is a matching pilot for any [Independent Starship] starship and while piloting one, may not draw more than one battle destiny here (your battle destinies here may not be canceled or modified by opponent).");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addKeywords(Keyword.GUNNER, Keyword.BOUNTY_HUNTER);
        setMatchingStarshipFilter(Filters.and(Filters.starship, Icon.INDEPENDENT));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter independentStarship = Filters.and(Filters.starship, Icon.INDEPENDENT);
        Condition pilotingIndependent = new PilotingCondition(self, independentStarship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), pilotingIndependent, 1, playerId));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.here(self), playerId, pilotingIndependent, opponent));
        modifiers.add(new MayNotModifyBattleDestinyModifier(self, Filters.here(self), playerId, pilotingIndependent, opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId,
                Filters.and(Filters.sameLocation(self), Filters.or(Filters.Nal_Hutta_system, Filters.Nal_Hutta_site)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
