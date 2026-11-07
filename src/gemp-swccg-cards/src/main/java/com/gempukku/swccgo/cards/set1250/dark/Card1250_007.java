package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_046;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardMatchingFilterModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToLostPileFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToLostPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromTableResult;

import java.util.List;

/** Legacy Block 5: Lord Sidious. */
public class Card1250_007 extends Card1116_046 {
    public Card1250_007() {
        setLore("Leader. Trade Federation.");
        setGameText("Whenever a Jedi is forfeited and you have exactly two Dark Jedi on table, retrieve 2 Force. While at your Senate and you have no senators here, Sidious is politics +4 and his game text may not be canceled. Immune to [Virtual Block 1] Leia and attrition.");
        addIcons(Icon.WARRIOR, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_5);
        addKeywords(Keyword.LEADER, Keyword.TRADE_FEDERATION);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    private Condition senateWithoutOwnSenator(PhysicalCard self) {
        return new AndCondition(new AtCondition(self, Filters.and(Filters.your(self), Filters.Galactic_Senate)),
                new NotCondition(new HereCondition(self, Filters.and(Filters.your(self), Filters.senator))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new PoliticsModifier(self, self, senateWithoutOwnSenator(self), 4),
                new ImmuneToCardMatchingFilterModifier(self, self, Filters.and(Filters.Leia, Icon.LEGACY_BLOCK_1)),
                new ImmuneToAttritionModifier(self));
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotHaveGameTextCanceledModifier(self, senateWithoutOwnSenator(self)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                  PhysicalCard self, int sourceId) {
        PhysicalCard forfeited = switch (result) {
            case ForfeitedCardToLostPileFromTableResult event -> event.getCard();
            case ForfeitedCardToUsedPileFromTableResult event -> event.getCard();
            case ForfeitedCardToLostPileFromOffTableResult event -> event.getCard();
            case ForfeitedCardToUsedPileFromOffTableResult event -> event.getCard();
            default -> null;
        };
        if (forfeited == null || !Filters.Jedi.accepts(game, forfeited)
                || Filters.filterAllOnTable(game, Filters.and(Filters.your(self), Filters.Dark_Jedi)).size() != 2) {
            return null;
        }
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Retrieve 2 Force");
        action.appendEffect(new RetrieveForceEffect(action, self.getOwner(), 2));
        return List.of(action);
    }
}
