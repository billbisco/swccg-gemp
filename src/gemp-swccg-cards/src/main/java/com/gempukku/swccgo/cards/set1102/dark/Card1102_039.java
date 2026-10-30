package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Alien
 * Title: Kitik Keed'kak
 */
public class Card1102_039 extends AbstractAlien {
    public Card1102_039() {
        super(Side.DARK, 2, 4, 8, 1, 4, "Kitik Keed'kak", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("A female insectoid Yam'rii. Very strong and easily angered. Known for stealth, but also has good technological aptitude. Meat eater. Loves eggs.");
        setGameText("During battle, may add 1 to your attrition against opponent for each opponent's character present. May not deploy to or board starfighters or enclosed vehicles.");
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.YAMRII);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.or(Filters.starfighter, Filters.enclosed_vehicle)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtTargetModifier(self, Filters.or(Filters.starfighter, Filters.enclosed_vehicle)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.present(self)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add to attrition");
            action.setActionMsg("Add 1 to attrition against opponent for each opponent's character present");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new AttritionModifier(self, Filters.here(self), new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.character)), opponent),
                            "Adds 1 to attrition against opponent for each opponent's character present"));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
