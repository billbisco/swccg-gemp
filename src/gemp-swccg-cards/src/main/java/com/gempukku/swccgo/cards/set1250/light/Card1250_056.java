package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5 Light, final printed Yarua (55E). */
public class Card1250_056 extends AbstractRepublic {
    public Card1250_056() {
        super(Side.LIGHT, 2, 4, 6, 2, 4, "Yarua", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setPolitics(2);
        setSpecies(Species.WOOKIEE);
        setLore("Kashyyyk's senior Wookiee senator. Believes that a thorough taxation plan will assist funding of other worthwhile Republic programs. Despises the corruption around him.");
        setGameText("Agenda: taxation. All Nudjs are lost. At end of your deploy phase, if with a Wookiee on Kashyyyk and you have 8 or more cards in hand, opponent loses 1 Force. Your Wookiees on Kashyyyk move for free using their landspeed.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeyword(Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AgendaModifier(self, Agenda.TAXATION));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new MovesForFreeUsingLandspeedModifier(self,
                Filters.and(Filters.your(self), Filters.Wookiee, Filters.on(Title.Kashyyyk))));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> nudjs = Filters.filterAllOnTable(game, Filters.title("Nudj"));
            if (!nudjs.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make all Nudjs lost");
                action.appendEffect(new LoseCardsFromTableEffect(action, nudjs, true));
                actions.add(action);
            }
        }
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.DEPLOY)
                && GameConditions.isOnSystem(game, self, Title.Kashyyyk)
                && GameConditions.isWith(game, self, Filters.Wookiee)
                && game.getGameState().getHand(self.getOwner()).size() >= 8) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));
            actions.add(action);
        }
        return actions;
    }
}
