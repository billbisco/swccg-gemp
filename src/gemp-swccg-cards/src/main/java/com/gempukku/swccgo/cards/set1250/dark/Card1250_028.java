package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/** Legacy Block 5: Trade Federation Tactics, errata 28E. */
public class Card1250_028 extends AbstractUsedInterrupt {
    public Card1250_028() {
        super(Side.DARK, 6, "Trade Federation Tactics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Organizations as wealthy as the Trade Federation can afford large amounts of military hardware, all purchased under the guise of protecting their commercial interests.");
        setGameText("Retarget an attempt to make your [Episode I] character 'hit' or lost to your other [Episode I] character present. OR Retarget opponent's just drawn destiny result about to make your starship 'hit' or lost to your character aboard; starship is protected from all such attempts this turn. OR deploy Sil Unch from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        var id = GameTextActionId.LEGACY__TRADE_FEDERATION_TACTICS__DEPLOY_SIL_UNCH;
        if (!GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) return null;
        var action = new PlayInterruptAction(game, self, id);
        action.setText("Deploy Sil Unch from Reserve Deck");
        action.allowResponses("Deploy Sil Unch from Reserve Deck", new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("Sil Unch"), true));
            }
        });
        return List.of(action);
    }
}
