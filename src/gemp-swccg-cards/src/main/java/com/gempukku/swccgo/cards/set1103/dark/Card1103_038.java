package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Effect
 * Title: Ket Maliss (V)
 */
public class Card1103_038 extends AbstractNormalEffect {
    public Card1103_038() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ket_Maliss, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Assassins are highly valued by Jabba the Hutt and other gangsters. Prince Xizor's 'shadow killer,' has unknown but undoubtedly lethal business in Mos Eisley.");
        setGameText("Deploy on table. Unless Emperor Palpatine on table, Black Sun agents are defense value +1 and forfeit +2. Once per turn, may take Beedo, Dannik Jerriko, Greedo, Hem Dazon, or Reegesk into hand from Reserve Deck; reshuffle. During your deploy phase, may deploy a docking bay from Reserve Deck; reshuffle. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.Black_Sun_agent, new NotCondition(new OnTableCondition(self, Filters.title("Emperor Palpatine"))), 1));
        modifiers.add(new ForfeitModifier(self, Filters.Black_Sun_agent, new NotCondition(new OnTableCondition(self, Filters.title("Emperor Palpatine"))), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId takeId = GameTextActionId.OVS__KET_MALISS__TAKE_ALIEN;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, takeId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {

            Filter aliens = Filters.or(Filters.title("Beedo"), Filters.persona(Persona.DANNIK), Filters.Greedo, Filters.title("Hem Dazon"), Filters.Reegesk);
            final TopLevelGameTextAction take = new TopLevelGameTextAction(self, gameTextSourceCardId, takeId);
            take.setText("Take alien into hand from Reserve Deck");
            take.setActionMsg("Take Beedo, Dannik Jerriko, Greedo, Hem Dazon, or Reegesk into hand from Reserve Deck");
            take.appendUsage(
                    new OncePerTurnEffect(take));
            take.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(take, playerId, aliens, true));
            actions.add(take);
        }

        GameTextActionId bayId = GameTextActionId.OVS__KET_MALISS__DEPLOY_DOCKING_BAY;
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, bayId)) {

            final TopLevelGameTextAction deploy = new TopLevelGameTextAction(self, gameTextSourceCardId, bayId);
            deploy.setText("Deploy docking bay from Reserve Deck");
            deploy.setActionMsg("Deploy a docking bay from Reserve Deck");
            deploy.appendEffect(
                    new DeployCardFromReserveDeckEffect(deploy, Filters.docking_bay, true));
            actions.add(deploy);
        }

        return actions;
    }
}
