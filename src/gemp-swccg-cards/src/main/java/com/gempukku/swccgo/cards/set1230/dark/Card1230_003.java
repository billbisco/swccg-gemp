package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeExcludedFromBattleResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Yorr
 */
public class Card1230_003 extends AbstractImperial {
    public Card1230_003() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Yorr, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Former member of Imperial Demonstration Team. Flew test flights during development of various TIE prototypes. Jendon's wingman. Has scored 24 combat victories.");
        setGameText("Adds 2 to power of anything he pilots (3 if piloting any starfighter). If your starship participating in a battle is about to be excluded (even if that starship may not participate in battle) or made lost by opponent, you may choose for it to be 'hit' instead.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1, Filters.starfighter));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CAPTAIN_YORR_V__HIT_INSTEAD;
        Filter yourStarship = Filters.and(Filters.your(self), Filters.starship);

        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_EXCLUDED_FROM_BATTLE) {
            final AboutToBeExcludedFromBattleResult result = (AboutToBeExcludedFromBattleResult) effectResult;
            final PhysicalCard starship = result.getCardToBeExcluded();
            final PreventableCardEffect preventable = result.getPreventableCardEffect();
            if (yourStarship.accepts(game, starship)
                    && preventable != null
                    && !preventable.isEffectOnCardPrevented(starship)) {
                return hitInsteadAction(self, playerId, gameTextSourceCardId, gameTextActionId, starship, preventable, "excluded from battle");
            }
        }

        if (TriggerConditions.isAboutToBeLost(game, effectResult, yourStarship)
                && GameConditions.isDuringBattle(game)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard starship = result.getCardToBeLost();
            final PreventableCardEffect preventable = result.getPreventableCardEffect();
            boolean byOpponent = opponent.equals(effectResult.getPerformingPlayerId())
                    || (result.getSourceCard() != null && opponent.equals(result.getSourceCard().getOwner()));
            if (byOpponent
                    && preventable != null
                    && Filters.at(Filters.battleLocation).accepts(game, starship)) {
                return hitInsteadAction(self, playerId, gameTextSourceCardId, gameTextActionId, starship, preventable, "lost");
            }
        }

        return null;
    }

    private List<OptionalGameTextTriggerAction> hitInsteadAction(PhysicalCard self, String playerId, int gameTextSourceCardId,
                                                                 GameTextActionId gameTextActionId, final PhysicalCard starship,
                                                                 final PreventableCardEffect preventable, String insteadOf) {
        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
        action.setText("'Hit' " + GameUtils.getFullName(starship) + " instead");
        action.setActionMsg("Make " + GameUtils.getCardLink(starship) + " 'hit' instead of being " + insteadOf);
        action.appendEffect(
                new PreventEffectOnCardEffect(action, preventable, starship, GameUtils.getCardLink(starship) + " is 'hit' instead"));
        action.appendEffect(
                new HitCardEffect(action, starship, self));
        return Collections.singletonList(action);
    }
}
