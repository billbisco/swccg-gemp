package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeSuspendedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TreatEffectOnCardAsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Eyes In The Dark (V)
 */
public class Card1240_085 extends AbstractNormalEffect {
    public Card1240_085() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Eyes In The Dark", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I don't like the look of this.'");
        setGameText("Deploy on a holosite. [Virtual Block] Wipe Them Out, All Of Them ignores this site. 'Effect' on Alter may be treated as 'Mantellian Savrip.' The game text of your Effects here may not be suspended. Except during battles here, may play your Interrupts here as if from hand.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    /** Any Virtual Block (Legacy) printing of Wipe Them Out, All Of Them. */
    static Filter virtualBlockWipeThemOut() {
        return Filters.and(Filters.title("Wipe Them Out, All Of Them"),
                Filters.or(Icon.LEGACY_BLOCK_1, Icon.LEGACY_BLOCK_2, Icon.LEGACY_BLOCK_3, Icon.LEGACY_BLOCK_4,
                        Icon.LEGACY_BLOCK_5, Icon.LEGACY_BLOCK_6, Icon.LEGACY_BLOCK_7, Icon.LEGACY_BLOCK_8,
                        Icon.LEGACY_BLOCK_9, Icon.LEGACY_BLOCK_D));
    }

    @Override
    protected boolean mayDeployOnHolosite() {
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.holosite;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, virtualBlockWipeThemOut(), Filters.here(self)));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(virtualBlockWipeThemOut(), Filters.attachedTo(Filters.here(self)))));
        // 'Effect' on Alter may be treated as 'Mantellian Savrip' (so any Alter may target a Savrip here, even while it is a dejarik)
        modifiers.add(new TreatEffectOnCardAsModifier(self, Filters.Alter, Filters.and(Filters.Mantellian_Savrip, Filters.here(self))));
        // Your Effects here (including Effects deployed here as dejariks under 'Dejarik Rules') may not be suspended
        modifiers.add(new MayNotBeSuspendedModifier(self, Filters.and(Filters.your(self), Filters.here(self),
                Filters.or(Filters.Effect, Filters.and(Filters.dejarikHologramAtHolosite, CardCategory.EFFECT, CardSubtype.NORMAL)))));
        // Except during battles here, may play your Interrupts here (deployed as dejariks) as if from hand
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.your(self), Filters.Interrupt, Filters.dejarikHologramAtHolosite, Filters.here(self)),
                new NotCondition(new DuringBattleAtCondition(Filters.here(self)))));
        return modifiers;
    }
}
