package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToLostPileFromTableResult;

import java.util.List;

/** General Bob Hudsol, Virtual Block 7.1. */
public class Card1271_033 extends AbstractRebel {
    private static final Filter INTERIOR_YAVIN = Filters.and(Filters.Yavin_4_site, Filters.interior_site);
    private static final String SENTRY = "Massassi Base Sentry";

    public Card1271_033() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "General Bob Hudsol", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Typical of hard-line Corellian officers known for caution in battle. Leader of resistance in Bothan space. Developed strong ties to Bothan spynet.");
        setGameText("While at an interior Yavin 4 site, Massassi Base Sentry is [Immune to Alter], and, once per turn, during battle at a 'liberated' system (either opponent's or one with only one [Dark Side]), except Yavin 4, may place your just forfeited card in Used Pile.");
        addIcons(Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, Filters.title(SENTRY), new AtCondition(self, INTERIOR_YAVIN), Title.Alter));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!(result instanceof ForfeitedCardToLostPileFromTableResult forfeiture)
                || !Filters.at(INTERIOR_YAVIN).accepts(game, self)
                || !GameConditions.isOncePerTurn(game, self, playerId, sourceId)) return null;

        Filter eligibleSystem = Filters.and(LegacyFilters.liberatedSystem, Filters.not(Filters.Yavin_4_system),
            Filters.or(Filters.opponents(self), Filters.iconCount(Icon.DARK_FORCE, 1)));
        PhysicalCard card = forfeiture.getCard();
        if (!TriggerConditions.justForfeited(game, result, playerId, Filters.and(Filters.your(self), card))
                || !GameConditions.isDuringBattleAt(game, eligibleSystem)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, sourceId);
        action.setText("Place your forfeited card in Used Pile");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, card, true));
        return List.of(action);
    }
}
