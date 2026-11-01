package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeSuspendedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Effect
 * Title: Eyes In The Dark
 */
public class Card1113_006 extends AbstractNormalEffect {
    public Card1113_006() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Eyes In The Dark", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I don't like the look of this.'");
        setGameText("Deploy on a holosite. [Virtual Set 9] Wipe Them Out, All Of Them ignores this site. 'Effect' on Alter may be treated as 'Mantellian Savrip.' The game text of your Effects here may not be suspended. Except during battles here, may play your Interrupts here as if from hand.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    private Filter virtualSet9WipeThemOut() {
        return Filters.and(Filters.title("Wipe Them Out, All Of Them"), new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_9;
            }
        });
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.holosite;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, virtualSet9WipeThemOut(), Filters.here(self)));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(virtualSet9WipeThemOut(), Filters.attachedTo(Filters.here(self)))));
        modifiers.add(new MayNotBeSuspendedModifier(self, Filters.and(Filters.your(self), Filters.Effect, Filters.here(self))));
        return modifiers;
    }
}
