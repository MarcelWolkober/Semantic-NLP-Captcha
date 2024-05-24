<template>
  <div class="ListChallengeView">
    <ListChallengeComponent :_-i-d="ID" :_lemma="lemma" :_reference-usage="referenceUsage" :_usages="usages" />
  </div>
</template>


<script>
import ListChallengeComponent from "@/components/ListChallengeComponent.vue";


export default {
  name: "ListChallengeView",
  components: { ListChallengeComponent },
  data() {
    return {
      ID: 1,
      lemma: "test",
      referenceUsage: {
        ID: 1,
        lemma: "test",
        context: "This is a test",
        posStartIndex: 1, posEndIndex: 5
      },
      usages: [
        { ID: 2, lemma: "test", context: "This is a test 1", posStartIndex: 0, posEndIndex: 4 },
        { ID: 4, lemma: "test", context: "This is a test 2", posStartIndex: 0, posEndIndex: 4 },
        { ID: 3, lemma: "test", context: "This is a test 3", posStartIndex: 0, posEndIndex: 4 }
      ]
    };
  }
};
</script>


<style scoped>

</style>