<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
      <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNavDropdown">
          <ul class="navbar-nav mx-auto">
            <li class="nav-item">
              <nuxt-link :to="`/repairmans/${this.$auth.user.username}`" class="nav-link mx-2">Profile</nuxt-link>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="mx-auto" style="width: 600px; margin-top: 50px;">
      <b-container class="h-100">
        <h4>Repairman Details:</h4>
        <p>Username: {{ repairman.username }}</p>
        <p>Name: {{ repairman.name }}</p>
        <p>Email: {{ repairman.email }}</p>
        <h4>Occorrences associated:</h4>
        <b-table v-if="occurencesAssociated.length" striped over :items="occurencesAssociated"
          :fields="occurrenceFields">
          <template v-slot:cell(client)="row">
            {{ row.item.client.name }}
          </template>
          <template v-slot:cell(document)="row">
            {{ row.item.document.filename }}
          </template>
          <template v-slot:cell(repairmen)="row">
            <template v-for="repairman in row.item.repairmen">
              - {{ repairman.name }}
            </template>
          </template>
          <template v-slot:cell(actions)="row">
            <nuxt-link :to="`code/${row.item.code}`">
              Details
            </nuxt-link>
          </template>
        </b-table>
        <p v-else>No occorrences associated.</p>
      </b-container>
    </div>
  </div>

</template>
<script>
export default {
  data() {
    return {
      repairman: {},
      occurencesAssociated: [],
      occurrenceFields: [
        //'code',
        'description', 'insurance', 'status', 'client', 'repairmen', 'document', "actions"],
      occurences: null,
      message: null
    }
  },
  computed: {
    username() {
      return this.$route.params.username
    }
  },
  created() {
    this.$axios.$get(`api/repairmen/${this.username}`)
      .then(repairman => this.repairman = repairman || {})
      .then(this.$axios.$get(`/api/repairmen/${this.username}/occurrences`)
        .then(occurrences => {
          this.occurencesAssociated = occurrences
        }))
  },
  methods: {
    signOut() {
      this.$auth.logout()
      this.$router.push('/')
    }
  }
}
</script>

<style>

</style>
