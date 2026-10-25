package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Places the bottom cards of a Lost Pile out of play.
 */
public class PlaceBottomCardsOfLostPileOutOfPlayEffect extends AbstractSubActionEffect {
    private final String _pileOwner;
    private final int _count;

    public PlaceBottomCardsOfLostPileOutOfPlayEffect(Action action, String pileOwner, int count) {
        super(action);
        _pileOwner = pileOwner;
        _count = count;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return game.getGameState().getLostPile(_pileOwner).size() >= _count;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame swccgGame) {
                        GameState gameState = swccgGame.getGameState();
                        List<PhysicalCard> lostPile = new ArrayList<PhysicalCard>(gameState.getCardPile(_pileOwner, Zone.LOST_PILE));
                        int fromIndex = Math.max(0, lostPile.size() - _count);
                        List<PhysicalCard> bottom = new ArrayList<PhysicalCard>(lostPile.subList(fromIndex, lostPile.size()));
                        if (!bottom.isEmpty()) {
                            subAction.appendEffect(
                                    new PlaceCardsOutOfPlayFromOffTableEffect(subAction, bottom));
                        }
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
