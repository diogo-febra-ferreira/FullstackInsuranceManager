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
              <nuxt-link :to="`/experts/${this.$auth.user.username}`" class="nav-link mx-2">Profile</nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link to="/occurrences" class="nav-link mx-2">Occurrences</nuxt-link>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <b-container>
      <b-table striped over :items="occurrences" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link class="btn btn-link" :to="`/occurrences/${row.item.code}`">Details
          </nuxt-link>
        </template>
        <template v-slot:cell(client)="row">
          {{ row.item.client.name }}
        </template>
      </b-table>
      <nuxt-link to="/">Back</nuxt-link>
    </b-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      fields: ['code', 'description', 'insurance', 'client', 'actions'],
      occurrences: [],
      expert: {}
    }
  },
  created() {
    this.$axios.$get(`api/experts/${this.$auth.user.username}`)
      .then(expert => {
        this.expert = expert || {}
        return this.$axios.$get('/api/occurrences/' + this.expert.insurance + '/show')
      })
      .then((occurrences) => {
        this.occurrences = occurrences
      })
  },
  methods: {
    signOut() {
      this.$auth.logout()
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>

</style>
