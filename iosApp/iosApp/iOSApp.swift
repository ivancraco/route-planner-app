import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinIosKt.doInitKoinIOS()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
            .onReceive(
            NotificationCenter.default.publisher(
            for: UIApplication.didBecomeActiveNotification
            )) { _ in KoinIosKt.onAppForeground()}
        }
    }
}