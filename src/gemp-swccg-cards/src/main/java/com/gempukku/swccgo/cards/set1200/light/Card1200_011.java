package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: There is Another
 */
public class Card1200_011 extends AbstractDefensiveShield {
    public Card1200_011() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "There is Another", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setLore("Blank");
        setGameText("Unless you have deployed a Skywalker this game, plays on Table. 'Luke' and 'Son of Vader' on opponent's (Death Star II) cards are treated as 'Leia'and 'Lady Vader,' respectively. Once during your turn, opponent may activate 1 Force.");
        addIcons(Icon.DEATH_STAR_II, Icon.REFLECTIONS_III);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.hasDeployedAtLeastXCardsThisGame(game, playerId, 1, Filters.Skywalker);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.opponents(self), Icon.DEATH_STAR_II), ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERE_IS_ANOTHER__OPPONENT_ACTIVATES_FORCE;

        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canActivateForce(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
