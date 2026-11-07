package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceRetrievalState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;
import java.util.List;

/** Legacy Block 6.1 DS-61-11 In Black 11. */
public class Card1261_057 extends AbstractStarfighter {
    public Card1261_057() {
        super(Side.DARK, 2, 3, 4, null, 3, null, 4, "DS-61-11 In Black 11",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Stationed aboard command ship Executor. Pilot known as 'Wampa' for icy precision in battle. Often serves as escort to Lord Vader's shuttle.");
        setGameText("Permanent pilot is •DS-61-11, who provides ability of 2. Whenever you deploy a TIE simultaneously with a pilot character, your Force retrieval from Sienar Fleet Systems is +1. Immune to attrition < 5.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return List.of(new AbstractPermanentAboard("DS-61-11", Uniqueness.UNIQUE, true, false, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String owner = self.getOwner();
        return List.of(
                new ForceRetrievalModifier(self, new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState state, ModifiersQuerying modifiers, PhysicalCard source) {
                        return isSienarRetrievalForTieDeployedWithPilot(state, modifiers, source, owner) ? 1 : 0;
                    }
                }, owner),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }

    /** Whether the Force retrieval in progress is from your Sienar Fleet Systems for your TIE that was just deployed
     * simultaneously with your pilot character. */
    private static boolean isSienarRetrievalForTieDeployedWithPilot(GameState state, ModifiersQuerying modifiers,
            PhysicalCard source, String owner) {
        if (source == null || !owner.equals(source.getOwner())
                || !Filters.Sienar_Fleet_Systems.accepts(state, modifiers, source))
            return false;
        ForceRetrievalState retrieval = state.getTopForceRetrievalState();
        if (retrieval == null)
            return false;
        Collection<PhysicalCard> involved = retrieval.getForceRetrievalEffect().getAdditionalCardsInvolvedInForceRetrieval();
        if (involved == null)
            return false;
        for (PhysicalCard card : involved) {
            PhysicalCard deployedWith = card.getDeployedSimultaneouslyWith();
            if (owner.equals(card.getOwner()) && Filters.TIE.accepts(state, modifiers, card)
                    && deployedWith != null && owner.equals(deployedWith.getOwner())
                    && Filters.pilot_character.accepts(state, modifiers, deployedWith))
                return true;
        }
        return false;
    }
}
