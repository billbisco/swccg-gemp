package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyInsurgentFilters;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Declaration Of Rebellion
 */
public class Card1272_020 extends AbstractNormalEffect {
    public Card1272_020() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Declaration Of Rebellion", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setGameText("Deploy on table. Bail, Leia, and Mon Mothma are Insurgents. While an Insurgent present at a battleground, your total battle destiny is +1, and, if with your non-unique character, each may forfeit for 4 when hit. Once per game may take one K'lor'slug into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    /** Your Insurgent present at a battleground. */
    static Filter yourInsurgentPresentAtBattleground(PhysicalCard self) {
        return Filters.and(Filters.your(self), LegacyInsurgentFilters.insurgent(), Filters.presentAt(Filters.battleground));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.character, Filters.or(Filters.Bail, Filters.Leia, Filters.Mon_Mothma)), Keyword.INSURGENT));
        // Center Of Tyranny / A Liberated World: "'1' on Declaration Of Rebellion is treated as '2'"
        modifiers.add(new TotalBattleDestinyModifier(self, new OnTableCondition(self, yourInsurgentPresentAtBattleground(self)),
                new ConditionEvaluator(1, 2, new GameTextModificationCondition(self, ModifyGameTextType.LEGACY__DECLARATION_OF_REBELLION__ONE_IS_TWO)), playerId));
        modifiers.add(new ForfeitValueToUseWhenForfeitedModifier(self,
                Filters.and(yourInsurgentPresentAtBattleground(self),
                        Filters.with(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.character)),
                        Filters.hit, Filters.not(Filters.forfeitValueMoreThan(3))), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DECLARATION_OF_REBELLION__TAKE_KLORSLUG;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take K'lor'slug into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Klorslug), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
