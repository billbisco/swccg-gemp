package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Bargaining Table
 */
public class Card1230_084 extends AbstractNormalEffect {
    public Card1230_084() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Bargaining_Table, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("The alcoves of Jabba's palace are good places to make deals and place bets. Jabba gets a cut of the profits, of course.");
        setGameText("Deploy on table. Your unique (•) aliens are forfeit +1 (+2 if they have printed deploy costs greater than or equal to their printed forfeit). During your deploy phase, may deploy one Attark, Crell, Loje, Momaw, Tzizvvt, or Vul from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter uniqueAliens = Filters.and(Filters.your(self), Filters.unique, Filters.alien);
        Filter printedDeployAtLeastForfeit = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Float deploy = physicalCard.getBlueprint().getDeployCost();
                Float forfeit = physicalCard.getBlueprint().getForfeit();
                return deploy != null && forfeit != null && deploy >= forfeit;
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, uniqueAliens, 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(uniqueAliens, printedDeployAtLeastForfeit), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BARGAINING_TABLE_V__DEPLOY_ALIEN;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter aliens = Filters.or(
                    Filters.title(Title.Attark),
                    Filters.titleContains("Crell"),
                    Filters.titleContains("Loje"),
                    Filters.Momaw_Nadon,
                    Filters.Tzizvvt,
                    Filters.titleContains("Vul"));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy alien from Reserve Deck");
            action.setActionMsg("Deploy Attark, Crell, Loje, Momaw, Tzizvvt, or Vul from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, aliens, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
