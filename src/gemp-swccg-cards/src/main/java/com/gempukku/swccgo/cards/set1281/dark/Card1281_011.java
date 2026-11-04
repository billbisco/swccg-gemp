package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Join Us Or Die
 */
public class Card1281_011 extends AbstractNormalEffect {
    public Card1281_011() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Join Us Or Die", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("Vader had to show Luke that giving in to the dark side of the Force was his only destiny.");
        setGameText("Deploy on your Throne Room. While Emperor and Vader here, Take Your Father's Place does not deplete Life Force or cause Force loss. Once per turn, may deploy So Be It, Jedi or a docking bay from Reserve Deck; reshuffle. 'Battle' on Insignificant Rebellion is treated as 'battle or duel.' (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Throne_Room);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition emperorAndVaderHere = new AndCondition(new HereCondition(self, Filters.Emperor), new HereCondition(self, Filters.Vader));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Take_Your_Fathers_Place, emperorAndVaderHere, ModifyGameTextType.LEGACY__JOIN_US_OR_DIE__DOES_NOT_DEPLETE_LIFE_FORCE));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Take_Your_Fathers_Place, emperorAndVaderHere, playerId));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Take_Your_Fathers_Place, emperorAndVaderHere, opponent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Insignificant_Rebellion, ModifyGameTextType.LEGACY__JOIN_US_OR_DIE__BATTLE_IS_BATTLE_OR_DUEL));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JOIN_US_OR_DIE__DEPLOY_SO_BE_IT_JEDI_OR_DOCKING_BAY;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy So Be It, Jedi or a docking bay from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.title("So Be It, Jedi"), Filters.docking_bay), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
